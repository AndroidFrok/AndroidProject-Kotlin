package com.hjq.demo.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.hjq.demo.http.api.UploadApi
import com.hjq.demo.http.model.CommonResp
import com.hjq.http.EasyHttp
import com.hjq.http.lifecycle.ActivityLifecycle
import com.hjq.http.lifecycle.ApplicationLifecycle
import com.hjq.http.listener.OnHttpListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * 日志上传工具类
 * 封装日志压缩、上传、清理功能
 */
object LogUploadUtil {

    /**
     * 上传结果回调
     */
    interface UploadCallback {
        /** 扫描完成，返回文件数量 */
        fun onScanComplete(fileCount: Int)

        /** 压缩完成 */
        fun onCompressSuccess(zipFile: File, fileCount: Int, sizeKB: Long)

        /** 压缩失败 */
        fun onCompressFailed(error: String)

        /** 上传成功 */
        fun onUploadSuccess(deletedCount: Int, sizeKB: Long)

        /** 上传失败 */
        fun onUploadFailed(error: String)
    }

    /**
     * 扫描、压缩并上传日志
     * @param context 上下文
     * @param scope 协程作用域
     * @param callback 回调接口
     */
    fun compressAndUploadLogs(
        context: Context, scope: CoroutineScope, callback: UploadCallback? = null
    ) {
        scope.launch(Dispatchers.IO) {
            try {
                // 获取日志目录
                val logDir = getLogPath(context)

                // 收集所有要压缩的文件及其在 zip 中的路径
                val filesToCompress = mutableListOf<Pair<File, String>>()

                // 1. 扫描日志目录
                if (logDir.exists() && logDir.isDirectory) {
                    val logFiles = logDir.listFiles()?.filter { it.isFile }
                    if (!logFiles.isNullOrEmpty()) {
                        logFiles.forEach { file ->
                            filesToCompress.add(file to "logs/${file.name}")
                        }
                        Timber.d("扫描日志目录: ${logFiles.size} 个文件")
                    }
                }

                // 2. 扫描 Documents 目录
                val documentsDir = File(context.getExternalFilesDir(null), "Documents")
                if (documentsDir.exists() && documentsDir.isDirectory) {
                    val docFiles = documentsDir.listFiles()?.filter { it.isFile }
                    if (!docFiles.isNullOrEmpty()) {
                        docFiles.forEach { file ->
                            filesToCompress.add(file to "documents/${file.name}")
                        }
                        Timber.d("扫描 Documents 目录: ${docFiles.size} 个文件")
                    }
                }

                withContext(Dispatchers.Main) {
                    callback?.onScanComplete(filesToCompress.size)
                }

                // 检查是否有文件要压缩
                if (filesToCompress.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        callback?.onCompressFailed("没有找到日志文件")
                    }
                    return@launch
                }

                // 创建 zip 文件
                val zipFile = File(logDir.parentFile, "logs_${System.currentTimeMillis()}.zip")
                val compressed = compressFilesToZip(filesToCompress, zipFile)

                if (compressed) {
                    val fileSizeKB = zipFile.length() / 1024
                    Timber.d("压缩成功: ${filesToCompress.size} 个文件, ${fileSizeKB}KB")

                    withContext(Dispatchers.Main) {
                        callback?.onCompressSuccess(zipFile, filesToCompress.size, fileSizeKB)
                        // 压缩成功后打开文件管理器
                        openFileLocation(context, zipFile.parentFile)
                        uploadLogFile(context, zipFile, callback)
                    }
                    scope.launch {
                    }
                    // 上传 zip 文件
                } else {
                    withContext(Dispatchers.Main) {
                        callback?.onCompressFailed("压缩失败")
                    }
                }

            } catch (e: Exception) {
                Timber.e(e, "压缩日志失败")
                withContext(Dispatchers.Main) {
                    callback?.onCompressFailed("压缩失败: ${e.message}")
                }
            }
        }
    }

    /**
     * 将多个文件压缩成 zip
     * @param files 要压缩的文件列表，Pair 中第一个是文件，第二个是 zip 中的路径
     * @param zipFile 输出的 zip 文件
     * @return 是否成功
     */
    fun compressFilesToZip(files: List<Pair<File, String>>, zipFile: File): Boolean {
        try {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
                val buffer = ByteArray(1024 * 8) // 8KB buffer

                files.forEach { (file, entryPath) ->
                    if (!file.exists() || !file.isFile) return@forEach

                    FileInputStream(file).use { fis ->
                        BufferedInputStream(fis).use { bis ->
                            val zipEntry = ZipEntry(entryPath)
                            zos.putNextEntry(zipEntry)

                            var len: Int
                            while (bis.read(buffer).also { len = it } > 0) {
                                zos.write(buffer, 0, len)
                            }

                            zos.closeEntry()
                        }
                    }
                    Timber.d("已压缩: $entryPath (${file.length()} bytes)")
                }
            }
            return true
        } catch (e: Exception) {
            Timber.e(e, "压缩文件失败")
            return false
        }
    }

    /**
     * 上传日志文件
     * @param context 上下文
     * @param file 要上传的文件
     * @param callback 回调接口
     */
    fun uploadLogFile(context: Context, file: File, callback: UploadCallback? = null) {
        if (!file.exists()) {
            callback?.onUploadFailed("文件不存在")
            return
        }

        val api = UploadApi()
        api.file = file

        EasyHttp.post(ApplicationLifecycle.getInstance()).api(api)
            .request(object : OnHttpListener<CommonResp> {
                override fun onSucceed(response: CommonResp?) {
                    if (response?.code == 1) {
                        // 上传成功，删除所有日志文件
                        val deletedCount = deleteLogFiles(context)
                        val sizeKB = file.length() / 1024
                        Timber.d("日志上传成功: ${file.name}, 删除 $deletedCount 个文件")
                        callback?.onUploadSuccess(deletedCount, sizeKB)
                    } else {
                        val sizeKB = file.length() / 1024
                        Timber.d("日志上传失败: ${response?.msg}")
                        callback?.onUploadFailed("${response?.msg} (${sizeKB}KB)")
                    }
                }

                override fun onFail(e: java.lang.Exception?) {
                    Timber.e(e, "日志上传失败")
                    callback?.onUploadFailed("上传失败: ${e?.message}")
                }

                override fun onEnd(call: Call?) {
                    super.onEnd(call)
                }
            })
    }

    /**
     * 删除所有已上传的日志文件
     * @param context 上下文
     * @return 删除的文件数量
     */
    fun deleteLogFiles(context: Context): Int {
        var deletedCount = 0

        try {
            // 1. 删除日志目录中的文件
            val logDir = getLogPath(context)
            if (logDir.exists() && logDir.isDirectory) {
                val logFiles = logDir.listFiles()?.filter { it.isFile }
                logFiles?.forEach { file ->
                    if (file.delete()) {
                        deletedCount++
                        Timber.d("已删除: ${file.absolutePath}")
                    }
                }
            }

            // 2. 删除 Documents 目录中的文件
            val documentsDir = File(context.getExternalFilesDir(null), "Documents")
            if (documentsDir.exists() && documentsDir.isDirectory) {
                val docFiles = documentsDir.listFiles()?.filter { it.isFile }
                docFiles?.forEach { file ->
                    if (file.delete()) {
                        deletedCount++
                        Timber.d("已删除: ${file.absolutePath}")
                    }
                }
            }

            Timber.d("共删除 $deletedCount 个日志文件")

        } catch (e: Exception) {
            Timber.e(e, "删除日志文件失败")
        }

        return deletedCount
    }

    /**
     * 仅压缩日志文件（不上传）
     * @param context 上下文
     * @param scope 协程作用域
     * @param onSuccess 成功回调
     * @param onFailed 失败回调
     */
    fun compressOnly(
        context: Context,
        scope: CoroutineScope,
        onSuccess: (File, Int, Long) -> Unit,
        onFailed: (String) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            try {
                val logDir = getLogPath(context)
                val filesToCompress = mutableListOf<Pair<File, String>>()

                // 扫描日志目录
                if (logDir.exists() && logDir.isDirectory) {
                    val logFiles = logDir.listFiles()?.filter { it.isFile }
                    if (!logFiles.isNullOrEmpty()) {
                        logFiles.forEach { file ->
                            filesToCompress.add(file to "logs/${file.name}")
                        }
                    }
                }

                // 扫描 Documents 目录
                val documentsDir = File(context.getExternalFilesDir(null), "Documents")
                if (documentsDir.exists() && documentsDir.isDirectory) {
                    val docFiles = documentsDir.listFiles()?.filter { it.isFile }
                    if (!docFiles.isNullOrEmpty()) {
                        docFiles.forEach { file ->
                            filesToCompress.add(file to "documents/${file.name}")
                        }
                    }
                }

                if (filesToCompress.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        onFailed("没有找到日志文件")
                    }
                    return@launch
                }

                val zipFile = File(logDir.parentFile, "logs_${System.currentTimeMillis()}.zip")
                val compressed = compressFilesToZip(filesToCompress, zipFile)

                if (compressed) {
                    val fileSizeKB = zipFile.length() / 1024
                    // 压缩成功后打开文件管理器
                    openFileLocation(context, zipFile.parentFile)
                    withContext(Dispatchers.Main) {
                        onSuccess(zipFile, filesToCompress.size, fileSizeKB)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onFailed("压缩失败")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "压缩日志失败")
                withContext(Dispatchers.Main) {
                    onFailed("压缩失败: ${e.message}")
                }
            }
        }
    }


    fun getLogPath(ctx: Context): File {
        // Android 10 (API 29) 及以上使用分区存储，无法在外部根目录创建文件夹
        // 使用应用专属外部目录，所有 Android 版本都支持
        // 路径: /storage/emulated/0/Android/data/com.jh2025.zhibingji/files/zhibingji_logs/
        val logDir = File(ctx.getExternalFilesDir(null), "zhibingji_logs")

        if (!logDir.exists()) {
            val created = logDir.mkdirs()
            if (!created) {
                Log.w(TAG, "无法创建外部存储目录，回退到内部存储: " + logDir.absolutePath)
                // 回退到内部存储
                return getInternalLogPath(ctx)
            }
        }

        // 验证目录是否可写
        if (logDir.exists() && logDir.canWrite()) {
            Log.d(TAG, "日志目录: " + logDir.absolutePath)
            return logDir
        } else {
            Log.w(TAG, "外部存储目录不可写，回退到内部存储: " + logDir.absolutePath)
            return getInternalLogPath(ctx)
        }
    }

    val TAG = "LogUploadUtil"

    /**
     * 获取内部存储日志目录作为回退方案
     */
    private fun getInternalLogPath(ctx: Context): File {
        val internalDir = File(ctx.filesDir, "logs")
        if (!internalDir.exists()) {
            internalDir.mkdirs()
        }
        Log.d(TAG, "使用内部日志目录: " + internalDir.absolutePath)
        return internalDir
    }

    fun getImageName(): String {
        val timeFormatter = SimpleDateFormat("yyyyMMdd", Locale.CHINA) //_HHmmss
        val time = System.currentTimeMillis()
        val imageName = timeFormatter.format(Date(time))
        return imageName
    }

    /**
     * 打开文件管理器并定位到指定目录
     * @param context 上下文
     * @param directory 要打开的目录
     */
    fun openFileLocation(context: Context, directory: File?) {
        if (directory == null || !directory.exists()) {
            Timber.w("目录不存在，无法打开文件管理器")
            return
        }

        try {
            // 方式1: 尝试使用 ACTION_VIEW 打开目录
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.fromFile(directory), "resource/folder")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            // 检查是否有应用可以处理此 Intent
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                Timber.d("已打开文件管理器: ${directory.absolutePath}")
                return
            }

            // 方式2: 尝试使用 CATEGORY_BROWSABLE 打开
            val intent2 = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.fromFile(directory)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (intent2.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent2)
                Timber.d("已打开文件管理器: ${directory.absolutePath}")
                return
            }

            // 方式3: 尝试打开父目录
            val parentDir = directory.parentFile
            if (parentDir != null && parentDir.exists()) {
                val intent3 = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.fromFile(parentDir)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent3)
                Timber.d("已打开父目录: ${parentDir.absolutePath}")
            } else {
                Timber.w("无法打开文件管理器: 没有找到可以处理的应用")
            }

        } catch (e: Exception) {
            Timber.e(e, "打开文件管理器失败")
        }
    }
}
