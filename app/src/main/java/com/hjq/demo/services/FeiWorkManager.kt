package com.hjq.demo.services

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.jh.knife.event.WorkerVersion
import java.util.concurrent.TimeUnit

class FeiWorkManager {
    companion object {
        val WorkerRefreshToken = "token"


        /**
         *  检查版本更新（延时回调方案 ）
         */
        fun checkVersion(ctx: Context) {

            //任务触发条件：充电中、网络已连接、电量充足
            val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
//            .setRequiresBatteryNotLow(true)
                .build()
            //创建任务请求
            val request = OneTimeWorkRequest.Builder(WorkerVersion::class.java)
                //设置触发条件
                .setConstraints(constraints)
                //设置延迟执行
//                .setInitialDelay(1, TimeUnit.MINUTES)
                .setInitialDelay(60, TimeUnit.SECONDS)
                //设置退避策略
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MINUTES
                )
                //设置标签
                .addTag(WorkerRefreshToken)
                //创建
                .build()

            //提交任务
//            WorkManager.getInstance(AppApplication.application).enqueue(request)
            WorkManager.getInstance(ctx)
                .enqueueUniqueWork(WorkerRefreshToken, ExistingWorkPolicy.REPLACE, request)
            //观察任务状态
            /*WorkManager.getInstance(AppApplication.application).getWorkInfoByIdLiveData(request.id)
                .observe(this) {
                    Timber.d(javaClass.name, "workInfo: $it")
                }*/
        }
    }

}