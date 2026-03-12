package com.hjq.demo.worktask

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hjq.demo.util.LogUploadUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * 日志上传 Worker
 * 使用 WorkManager 定期执行日志压缩上传任务
 *
 * 使用 PeriodicWorkRequest 设置最小间隔为 15 分钟
 * 这里设置为 2 小时执行一次
 */
class LogUploadWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                Timber.d("LogUploadWorker 开始执行日志上传任务")

                // 执行日志压缩上传
                LogUploadUtil.compressAndUploadLogs(
                    context,
                    this@withContext,
                    object : LogUploadUtil.UploadCallback {
                        override fun onScanComplete(fileCount: Int) {
                            Timber.d("扫描完成: $fileCount 个文件")
                        }

                        override fun onCompressSuccess(zipFile: java.io.File, fileCount: Int, sizeKB: Long) {
                            Timber.d("压缩成功: $fileCount 个文件, ${sizeKB}KB")
                        }

                        override fun onCompressFailed(error: String) {
                            Timber.e("压缩失败: $error")
                        }

                        override fun onUploadSuccess(deletedCount: Int, sizeKB: Long) {
                            Timber.d("上传成功: 删除 $deletedCount 个文件, ${sizeKB}KB")
                        }

                        override fun onUploadFailed(error: String) {
                            Timber.e("上传失败: $error")
                        }
                    }
                )

                // 返回成功，让 WorkManager 继续调度下一次任务
                Result.success()
            } catch (e: Exception) {
                Timber.e(e, "LogUploadWorker 执行失败")
                // 返回重试，让 WorkManager 稍后重试
                Result.retry()
            }
        }
    }

    companion object {
        /** 任务标签 */
        const val TAG_WORK_NAME = "log_upload_periodic_work"

        /** 任务唯一标识 */
        const val TAG = "log_upload_worker"
    }
}
