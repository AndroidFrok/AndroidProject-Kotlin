package com.hjq.demo.worktask

import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.hjq.base.CommonContext
import com.hjq.demo.app.AppApplication
import com.hjq.demo.manager.MmkvUtil
import com.hjq.demo.manager.SharePreferenceUtil
import com.hjq.demo.other.EventShowTime
import com.hjq.demo.other.RenewTipEvent
import com.hjq.demo.ui.activity.SplashActivity
import com.hjq.toast.ToastUtils
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 *  扫码支付后  创建定时回到应用的任务  为了更精确 任务层面 用秒作为单位  逻辑层和后端保持一致  用分钟做单位
 */
class TaskGoHome {


    companion object {
        const val tag = "task1_order_change";
        val tagTellU = "task2_tell_user_renew"; // 用于提示用户还有x分钟到期
        val orderStatus = "order_status";
        fun goMain(seconds: Long) {
            if (seconds < 60) {
                ToastUtils.show("时长不足60秒")
//                return;
            }
            Timber.i("任务 goMain $seconds")
            //任务触发条件：充电中、网络已连接、电量充足
            /*val constraints = Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
//            .setRequiresBatteryNotLow(true)
                .build()*/
            //创建任务请求
            val request = OneTimeWorkRequest.Builder(Work::class.java)
//            val pa = WorkerParameters()
//            val request = OneTimeWorkRequest.Builder(WorkGoMain(ctx))
                //设置触发条件
//                .setConstraints(constraints)
                //设置延迟执行
//                .setInitialDelay(1, TimeUnit.MINUTES)
                .setInitialDelay(seconds, TimeUnit.SECONDS)
                //设置退避策略
                /*.setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
                )*/
                //设置标签
                .addTag(tag)
                //创建
                .build()
//            val tipMiniute = MmkvUtil.getString(MmkvUtil.PreTipMiniutes, "0");
            val tipMiniute = SharePreferenceUtil.getSharePreferenceUtil(AppApplication.get())
                .getStrData(MmkvUtil.PreTipMiniutes, "0");
            if (seconds > 1) {
                tipYou(seconds - 60 * tipMiniute.toLong())
            }
            //提交任务
//            WorkManager.getInstance(AppApplication.application).enqueue(request)
            WorkManager.getInstance(CommonContext.getContext())
                .enqueueUniqueWork(tag, ExistingWorkPolicy.REPLACE, request)
//            通知主界面更新 时间
            EventBus.getDefault().post(EventShowTime("$seconds", "$tipMiniute"))
            //观察任务状态
//            WorkManager.getInstance(CommonContext.getContext()).getWorkInfosByTag(tag)

        }

        /**
         * 随着订单状态改变  不对外部开放此函数
         */
        private fun tipYou(seconds: Long) {
            Timber.d("任务 tagTellU ")
            //任务触发条件：充电中、网络已连接、电量充足
            /*val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
                .build()*/
            //创建任务请求
            val request = OneTimeWorkRequest.Builder(Work::class.java)
//            val pa = WorkerParameters()
//            val request = OneTimeWorkRequest.Builder(WorkGoMain(ctx))
                //设置触发条件
//                .setConstraints(constraints)
                //设置延迟执行
//                .setInitialDelay(1, TimeUnit.MINUTES)
                .setInitialDelay(seconds, TimeUnit.SECONDS)
                //设置退避策略
                /*.setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
                )*/
                //设置标签
                .addTag(tagTellU)
                //创建
                .build()

            //提交任务
//            WorkManager.getInstance(AppApplication.application).enqueue(request)
            WorkManager.getInstance(CommonContext.getContext())
                .enqueueUniqueWork(tagTellU, ExistingWorkPolicy.REPLACE, request)
            //观察任务状态
            /*WorkManager.getInstance(AppApplication.application).getWorkInfoByIdLiveData(request.id)
                .observe(this) {
                    Timber.d(javaClass.name, "workInfo: $it")
                }*/
        }
    }


    class Work(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
        val c = ctx;
        val p = params;
        override fun doWork(): Result {
            Timber.d("WorkGoMain doWork ${p.tags}")
            var iterator = p.tags.iterator();
            var tagg = "";
            while (iterator.hasNext()) {
                val s = iterator.next();
                if (s.startsWith("task")) {//   14:23   28
                    tagg = s;
                }
            }
            Timber.d("执行任务 $tagg")
            when (tagg) {
                tag -> {
                    EventBus.getDefault().post(RenewTipEvent("订单已结束，欢迎使用"))
                    //        发送广播  不太稳定
                    val st = Intent(tag);
                    st.putExtra(orderStatus, false)
                    c.sendBroadcast(st);
//  目前较稳定方案
                    val intent = Intent(CommonContext.getContext(), SplashActivity::class.java);
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    c.startActivity(intent)
                }

                tagTellU -> {
//                    播报语音
                    ToastUtils.show("请续费");
                    EventBus.getDefault().post(RenewTipEvent())
                }
            }
            return Result.success();
        }


    }
}