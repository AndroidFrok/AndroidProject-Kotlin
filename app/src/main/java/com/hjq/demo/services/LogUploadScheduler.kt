package com.hjq.demo.worktask

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.hjq.base.CommonContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * 日志上传任务调度器
 * 负责启动和停止日志上传的定期任务
 *
 *         // 启动定期日志上传任务（每 2 小时执行一次）
 *         LogUploadScheduler.schedulePeriodicUpload()
 */
object LogUploadScheduler {

    /**
     * 启动定期日志上传任务
     * 每 2 小时执行一次
     *
     * 注意：PeriodicWorkRequest 的最小间隔是 15 分钟
     */
    fun schedulePeriodicUpload() {
        val context = CommonContext.getContext()

        // 设置任务约束条件
        val constraints = Constraints.Builder()
            // 需要网络连接时才执行（可选：设置为 UNMETERED 表示仅在 WiFi 下）
//            .setRequiredNetworkType(NetworkType.CONNECTED)
            // 设备不在省电模式时执行
//            .setRequiresBatteryNotLow(false)
            // 设备充电时执行（可选）
//            .setRequiresCharging(false)
            .build()

        // 创建定期任务请求
        // PeriodicWorkRequest 最小间隔为 15 分钟
        // 这里设置为 2 小时（120 分钟）
        // flexTimeInterval 是任务可以在周期内执行的灵活时间窗口，设置为最小值
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            LogUploadWorker::class.java,
            2,  //todo 重复间隔：2 小时
            TimeUnit.HOURS,
            40, // flex 时间间隔：最小值 15 分钟
            TimeUnit.MINUTES
        )
            // 设置约束条件
            .setConstraints(constraints)
            // 添加标签，方便后续查询或取消
            .addTag(LogUploadWorker.TAG)
            .build()

        // 使用 enqueueUniquePeriodicWork 确保只有一个任务在运行
        // ExistingPeriodicWorkPolicy.KEEP：如果已存在相同任务，保留不替换
        // ExistingPeriodicWorkPolicy.REPLACE：如果已存在相同任务，替换为新的
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                LogUploadWorker.TAG_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )

        Timber.d("日志上传定期任务已启动: 每 2 小时执行一次")
    }

    /**
     * 取消日志上传任务
     */
    fun cancelPeriodicUpload() {
        val context = CommonContext.getContext()
        WorkManager.getInstance(context)
            .cancelUniqueWork(LogUploadWorker.TAG_WORK_NAME)

        Timber.d("日志上传定期任务已取消")
    }

    /**
     * 取消所有带有指定标签的任务
     */
    fun cancelAllByTag() {
        val context = CommonContext.getContext()
        WorkManager.getInstance(context)
            .cancelAllWorkByTag(LogUploadWorker.TAG)

        Timber.d("所有标签为 ${LogUploadWorker.TAG} 的任务已取消")
    }

    /**
     * 检查任务是否已调度
     * 注意：这个方法需要在协程或其他异步上下文中调用
     */
    suspend fun isScheduled(): Boolean {
        val context = CommonContext.getContext()
        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosByTag(LogUploadWorker.TAG)
            .get()

        return workInfos.isNotEmpty()
    }

    /**
     * 获取任务状态信息
     */
    suspend fun getWorkStatus(): String {
        val context = CommonContext.getContext()
        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosByTag(LogUploadWorker.TAG)
            .get()

        if (workInfos.isEmpty()) {
            return "任务未调度"
        }

        val workInfo = workInfos[0]
        val state = workInfo.state.toString()
        val isRunning = workInfo.state.name == "RUNNING"
        val isEnqueued = workInfo.state.name == "ENQUEUED"

        return buildString {
            append("状态: $state")
            append(", 运行中: $isRunning")
            append(", 队列中: $isEnqueued")
        }
    }
}
