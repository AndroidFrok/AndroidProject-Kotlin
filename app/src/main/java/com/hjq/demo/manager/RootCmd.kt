package com.hjq.demo.manager

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

/**
 * Root 命令执行工具类
 *
 * 功能：
 * - 检测设备是否已 root
 * - 执行 root 命令
 * - 执行普通 shell 命令
 * - 支持同步/异步执行
 */
object RootCmd {

    private const val TAG = "RootCmd"

    /**
     * 检测设备是否已 root
     * @return true 表示已获取 root 权限
     */
    fun isRoot(): Boolean {
        return execRootCmd("echo test").contains("test")
    }

    /**
     * 全面检测设备是否已 root（多种检测方式）
     *
     * 检测方式：
     * 1. 检查 su 二进制文件是否存在
     * 2. 检查常见 root 管理应用是否已安装
     * 3. 检查系统关键路径是否可写
     * 4. 尝试执行 su 命令
     *
     * @return true 表示设备已 root
     */
    fun isAppRoot(): Boolean {
        // 方式1: 检查 su 二进制文件是否存在
        val suPaths = listOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )

        for (path in suPaths) {
            if (execShellCmd("ls $path").trim().isNotEmpty()) {
                Log.d(TAG, "Found su at: $path")
                return true
            }
        }

        // 方式2: 检查常见 root 管理应用是否已安装
        val rootApps = listOf(
            "com.noshufou.android.su",
            "com.thirdparty.superuser",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.topjohnwu.magisk",
            "com.kingroot.kinguser",
            "com.s.mediatek",
            "com.zhiqupk.root",
            "com.alephzain.framework",
            "com.kingouser.com",
            "com.android.salexander"
        )

        for (pkg in rootApps) {
            val result = execShellCmd("pm path $pkg")
            if (result.contains("package:")) {
                Log.d(TAG, "Found root app: $pkg")
                return true
            }
        }

        // 方式3: 检查系统关键路径是否可写（需要 root）
        val writablePaths = listOf("/system", "/data")
        for (path in writablePaths) {
            val testCmd = "touch $path/.root_test 2>&1 && rm $path/.root_test 2>&1"
            val result = execRootCmd(testCmd)
            if (!result.contains("Permission denied") && !result.contains("Read-only")) {
                Log.d(TAG, "Path is writable: $path")
                return true
            }
        }

        // 方式4: 尝试执行 su 命令并获取 id
        val idResult = execRootCmd("id")
        if (idResult.contains("uid=0")) {
            Log.d(TAG, "Got root uid: $idResult")
            return true
        }

        Log.d(TAG, "Device is not rooted")
        return false
    }

    /**
     * 执行 root 命令（同步）
     * @param command 要执行的命令
     * @param needRoot 是否需要 root 权限
     * @return 命令执行结果
     */
    fun execCmd(command: String, needRoot: Boolean = true): String {
        return if (needRoot) {
            execRootCmd(command)
        } else {
            execShellCmd(command)
        }
    }

    /**
     * 执行 root 命令列表（同步）
     * @param commands 要执行的命令列表
     * @param needRoot 是否需要 root 权限
     * @return 命令执行结果
     */
    fun execCmd(commands: List<String>, needRoot: Boolean = true): String {
        return if (needRoot) {
            execRootCmd(commands)
        } else {
            execShellCmd(commands)
        }
    }

    /**
     * 执行 root 命令（异步，在 IO 线程）
     * @param command 要执行的命令
     * @param needRoot 是否需要 root 权限
     * @return 命令执行结果
     */
    suspend fun execCmdAsync(command: String, needRoot: Boolean = true): String = withContext(Dispatchers.IO) {
        execCmd(command, needRoot)
    }

    /**
     * 执行 root 命令列表（异步，在 IO 线程）
     * @param commands 要执行的命令列表
     * @param needRoot 是否需要 root 权限
     * @return 命令执行结果
     */
    suspend fun execCmdAsync(commands: List<String>, needRoot: Boolean = true): String = withContext(Dispatchers.IO) {
        execCmd(commands, needRoot)
    }

    /**
     * 执行 root 权限命令
     * @param command 单条命令
     * @return 执行结果
     */
    private fun execRootCmd(command: String): String {
        return execRootCmd(listOf(command))
    }

    /**
     * 执行 root 权限命令列表
     * @param commands 命令列表
     * @return 执行结果
     */
    private fun execRootCmd(commands: List<String>): String {
        var process: Process? = null
        var os: DataOutputStream? = null
        var result = StringBuilder()

        try {
            // 申请 su 权限
            process = Runtime.getRuntime().exec("su")
            os = DataOutputStream(process.outputStream)

            // 执行命令
            for (command in commands) {
                if (command.isNotEmpty()) {
                    Log.d(TAG, "execRootCmd: $command")
                    os.write(command.toByteArray())
                    os.writeBytes("\n")
                    os.flush()
                }
            }

            // 退出
            os.writeBytes("exit\n")
            os.flush()

            // 获取执行结果
            process.waitFor()
            val exitValue = process.exitValue()
            Log.d(TAG, "exitValue: $exitValue")

            // 读取成功输出
            val successReader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (successReader.readLine().also { line = it } != null) {
                result.append(line).append("\n")
            }

            // 读取错误输出
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            val errorResult = StringBuilder()
            while (errorReader.readLine().also { line = it } != null) {
                errorResult.append(line).append("\n")
            }

            // 如果有错误输出，拼接在结果后面
            if (errorResult.isNotEmpty()) {
                result.append("ERROR:\n").append(errorResult)
            }

        } catch (e: Exception) {
            Log.e(TAG, "execRootCmd error", e)
            result.append("Exception: ").append(e.message)
        } finally {
            try {
                os?.close()
                process?.destroy()
            } catch (e: Exception) {
                Log.e(TAG, "close error", e)
            }
        }

        val resultStr = result.toString()
        Log.d(TAG, "execRootCmd result: $resultStr")
        return resultStr
    }

    /**
     * 执行普通 shell 命令（不需要 root）
     * @param command 单条命令
     * @return 执行结果
     */
    private fun execShellCmd(command: String): String {
        return execShellCmd(listOf(command))
    }

    /**
     * 执行普通 shell 命令列表（不需要 root）
     * @param commands 命令列表
     * @return 执行结果
     */
    private fun execShellCmd(commands: List<String>): String {
        var process: Process? = null
        var result = StringBuilder()

        try {
            // 使用 sh 执行命令
            val cmd = StringBuilder("sh")
            for (command in commands) {
                if (command.isNotEmpty()) {
                    cmd.append(" -c \"").append(command).append("\"")
                }
            }

            Log.d(TAG, "execShellCmd: $cmd")
            process = Runtime.getRuntime().exec(cmd.toString())

            // 获取执行结果
            process.waitFor()

            // 读取输出
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                result.append(line).append("\n")
            }

            // 读取错误输出
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            val errorResult = StringBuilder()
            while (errorReader.readLine().also { line = it } != null) {
                errorResult.append(line).append("\n")
            }

            if (errorResult.isNotEmpty()) {
                result.append("ERROR:\n").append(errorResult)
            }

        } catch (e: Exception) {
            Log.e(TAG, "execShellCmd error", e)
            result.append("Exception: ").append(e.message)
        } finally {
            process?.destroy()
        }

        val resultStr = result.toString()
        Log.d(TAG, "execShellCmd result: $resultStr")
        return resultStr
    }

    /**
     * 重启设备（需要 root）
     */
    fun reboot() {
        execRootCmd("reboot")
    }

    /**
     * 重启到 recovery 模式（需要 root）
     */
    fun rebootRecovery() {
        execRootCmd("reboot recovery")
    }

    /**
     * 重启到 bootloader 模式（需要 root）
     */
    fun rebootBootloader() {
        execRootCmd("reboot bootloader")
    }

    /**
     * 关机（需要 root）
     */
    fun shutdown() {
        execRootCmd(listOf("reboot -p"))
    }

    /**
     * 安装 APK（需要 root）
     * @param apkPath APK 文件路径
     * @return 执行结果
     */
    fun installApk(apkPath: String): String {
        return execRootCmd("pm install -r $apkPath")
    }

    /**
     * 卸载应用（需要 root）
     * @param packageName 包名
     * @return 执行结果
     */
    fun uninstallApp(packageName: String): String {
        return execRootCmd("pm uninstall $packageName")
    }

    /**
     * 清除应用数据（需要 root）
     * @param packageName 包名
     * @return 执行结果
     */
    fun clearAppData(packageName: String): String {
        return execRootCmd("pm clear $packageName")
    }

    /**
     * 设置文件权限（需要 root）
     * @param path 文件路径
     * @param permission 权限（如 777、644 等）
     * @return 执行结果
     */
    fun chmod(path: String, permission: String): String {
        return execRootCmd("chmod $permission $path")
    }

    /**
     * 修改文件所有者（需要 root）
     * @param path 文件路径
     * @param owner 所有者（如 root:root）
     * @return 执行结果
     */
    fun chown(path: String, owner: String): String {
        return execRootCmd("chown $owner $path")
    }

    /**
     * 挂载系统为读写（需要 root）
     * @return 执行结果
     */
    fun mountSystemRW(): String {
        return execRootCmd("mount -o remount,rw /system")
    }

    /**
     * 挂载系统为只读（需要 root）
     * @return 执行结果
     */
    fun mountSystemRO(): String {
        return execRootCmd("mount -o remount,ro /system")
    }

    /**
     * 获取设备属性
     * @param prop 属性名
     * @return 属性值
     */
    fun getProp(prop: String): String {
        return execShellCmd("getprop $prop").trim()
    }

    /**
     * 设置设备属性（需要 root）
     * @param prop 属性名
     * @param value 属性值
     * @return 执行结果
     */
    fun setProp(prop: String, value: String): String {
        return execRootCmd("setprop $prop $value")
    }

    /**
     * 杀死进程（需要 root）
     * @param pid 进程 ID
     * @return 执行结果
     */
    fun killPid(pid: String): String {
        return execRootCmd("kill -9 $pid")
    }

    /**
     * 根据包名杀死应用（需要 root）
     * @param packageName 包名
     * @return 执行结果
     */
    fun killPackage(packageName: String): String {
        return execRootCmd("am force-stop $packageName")
    }

    /**
     * 启动应用
     * @param packageName 包名
     * @param ActivityName Activity 完整名称（可选）
     * @return 执行结果
     */
    fun startApp(packageName: String, ActivityName: String? = null): String {
        val cmd = if (ActivityName != null) {
            "am start -n $packageName/$ActivityName"
        } else {
            "monkey -p $packageName -c android.intent.category.LAUNCHER 1"
        }
        return execShellCmd(cmd)
    }

    // ==================== 应用前台检测相关方法 ====================

    /**
     * 获取当前前台应用的包名
     * @return 当前前台应用包名，获取失败返回空字符串
     */
    fun getCurrentForegroundApp(): String {
        return try {
            // 使用 dumpsys activity top 获取当前前台 Activity
            val result = execShellCmd("dumpsys activity top")
            val lines = result.lines()

            // 查找 ACTIVITY 或 mResumedActivity 行
            for (line in lines) {
                if (line.contains("ACTIVITY") && line.contains("/") && !line.contains("waiting")) {
                    // 格式: ACTIVITY com.example.app/com.example.app.MainActivity xxx
                    val parts = line.trim().split(" ")
                    for (part in parts) {
                        if (part.contains("/")) {
                            return part.substringBefore("/")
                        }
                    }
                }
                // Android 5.0+ 使用 mResumedActivity
                if (line.contains("mResumedActivity")) {
                    // 格式: mResumedActivity: ActivityRecord{xxx u0 com.example.app/com.example.app.MainActivity txxx}
                    val match = Regex("[a-z0-9]+\\.[a-z0-9]+(\\.[a-z0-9]+)+/").find(line)
                    if (match != null) {
                        return match.value.substringBefore("/")
                    }
                }
            }

            // 降级方案：使用 ps 命令查找前台进程
            val psResult = execShellCmd("ps -A -o NAME,ARGS | grep surfaceflinger")
            Log.w(TAG, "Fallback to ps command")
            ""
        } catch (e: Exception) {
            Log.e(TAG, "getCurrentForegroundApp error", e)
            ""
        }
    }

    /**
     * 获取当前前台应用的包名（使用 dumpsys activity activities）
     * @return 当前前台应用包名，获取失败返回空字符串
     */
    fun getCurrentForegroundAppV2(): String {
        return try {
            val result = execShellCmd("dumpsys activity activities | grep mResumedActivity")
            // 格式: mResumedActivity: ActivityRecord{xxx u0 com.example.app/com.example.app.MainActivity txxx}
            val match = Regex("([a-z0-9]+\\.[a-z0-9]+(?:\\.[a-z0-9]+)+/)?").find(result)
            if (match != null && match.value.contains("/")) {
                match.value.substringBefore("/")
            } else {
                ""
            }
        } catch (e: Exception) {
            Log.e(TAG, "getCurrentForegroundAppV2 error", e)
            ""
        }
    }

    /**
     * 判断指定应用是否处于前台（使用 shell 命令）
     * @param packageName 应用包名
     * @return true 表示应用在前台
     */
    fun isAppForeground(packageName: String): Boolean {
        val currentApp = getCurrentForegroundApp()
        return currentApp == packageName
    }

    /**
     * 判断指定应用是否处于前台（使用 dumpsys activity activities）
     * @param packageName 应用包名
     * @return true 表示应用在前台
     */
    fun isAppForegroundV2(packageName: String): Boolean {
        val currentApp = getCurrentForegroundAppV2()
        return currentApp == packageName
    }

    /**
     * 判断指定应用是否处于前台（使用 ActivityManager API）
     * @param context Context
     * @param packageName 应用包名
     * @return true 表示应用在前台
     */
    fun isAppForeground(context: Context, packageName: String): Boolean {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val runningTasks = activityManager?.getRunningTasks(1)
            if (runningTasks != null && runningTasks.isNotEmpty()) {
                val topActivity = runningTasks[0].topActivity
                topActivity?.packageName == packageName
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "isAppForeground error", e)
            false
        }
    }

    /**
     * 异步判断指定应用是否处于前台
     * @param packageName 应用包名
     * @return true 表示应用在前台
     */
    suspend fun isAppForegroundAsync(packageName: String): Boolean = withContext(Dispatchers.IO) {
        isAppForeground(packageName)
    }

    /**
     * 获取所有正在运行的应用列表
     * @return 正在运行的应用包名列表
     */
    fun getRunningApps(): List<String> {
        return try {
            val result = execShellCmd("ps -A")
            val lines = result.lines()
            val apps = mutableSetOf<String>()

            for (line in lines) {
                // 跳过标题行
                if (line.contains("USER") || line.contains("PID")) continue

                // 解析包名（通常是最后一列或倒数第二列）
                val parts = line.trim().split("\\s+".toRegex())
                if (parts.size >= 2) {
                    val lastPart = parts.last()
                    // 过滤掉系统进程和 shell 进程
                    if (lastPart.contains(".") &&
                        !lastPart.startsWith("/") &&
                        !lastPart.startsWith("[") &&
                        lastPart != "ps" &&
                        !lastPart.startsWith("android.")) {
                        apps.add(lastPart)
                    }
                }
            }

            apps.toList()
        } catch (e: Exception) {
            Log.e(TAG, "getRunningApps error", e)
            emptyList()
        }
    }

    /**
     * 获取当前前台应用的详细信息（包名和 Activity）
     * @return Pair<包名, Activity名>，获取失败返回 null
     */
    fun getForegroundAppInfo(): Pair<String, String>? {
        return try {
            val result = execShellCmd("dumpsys activity top")
            val lines = result.lines()

            for (line in lines) {
                if (line.contains("ACTIVITY") && line.contains("/") && !line.contains("waiting")) {
                    // 格式: ACTIVITY com.example.app/com.example.app.MainActivity xxx
                    val parts = line.trim().split(" ")
                    for (part in parts) {
                        if (part.contains("/")) {
                            val packageName = part.substringBefore("/")
                            val activityName = part.substringAfter("/").substringBefore(" ")
                            return Pair(packageName, activityName)
                        }
                    }
                }
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "getForegroundAppInfo error", e)
            null
        }
    }

    /**
     * 判断当前应用是否在前台（使用 Application Context）
     * @param context Context
     * @return true 表示当前应用在前台
     */
    fun isCurrentAppForeground(context: Context): Boolean {
        return try {
            val packageName = context.packageName
            isAppForeground(context, packageName)
        } catch (e: Exception) {
            Log.e(TAG, "isCurrentAppForeground error", e)
            false
        }
    }

    /**
     * 异步判断当前应用是否在前台
     * @param context Context
     * @return true 表示当前应用在前台
     */
    suspend fun isCurrentAppForegroundAsync(context: Context): Boolean = withContext(Dispatchers.IO) {
        isCurrentAppForeground(context)
    }
}
