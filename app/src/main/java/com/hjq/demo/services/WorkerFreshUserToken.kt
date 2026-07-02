package com.jh.knife.event

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.hjq.demo.http.api.VersionApi
import com.hjq.demo.http.model.VersionResp
import com.hjq.demo.other.AppConfig
import com.hjq.http.EasyHttp
import com.hjq.http.EasyUtils
import com.hjq.http.lifecycle.ApplicationLifecycle
import com.hjq.http.listener.HttpCallback
import okhttp3.Call
import timber.log.Timber

/**
 *  每60秒一个任务
 */
class WorkerVersion(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    var c = ctx
    override fun doWork(): Result {
//        Timber.i("版本更新检测！")
//        WorkUtil.checkVersion(c)
        return try {
            EasyUtils.runOnMainThread {
                req(c)
            }
            Result.success()
        } catch (throwable: Throwable) {
            Timber.e("运行失败" + throwable.localizedMessage)
            Result.failure()
        }
    }

    private fun req(ctx: Context) {
//        通知登录界面  干点事
//        EventBus.getDefault().post(Adapter2UI(1, ""))

        EasyHttp.post(ApplicationLifecycle.getInstance()).api(
            VersionApi()
        ).request(object : HttpCallback<VersionResp>(null) {
            override fun onEnd(call: Call) {
            }

            override fun onSucceed(data: VersionResp) {
                if (data.code != 1) {
                    return
                }
                if (data.data != null) {
                    val data = data.data
                    if (data != null) {
                        if (data.version_code != null && !data.version_code.equals("") && (data?.version_code?.toInt()!! > AppConfig.getVersionCode())) {
//                            EventBus.getDefault().post(Adapter2UI(0, data.download_url))
                        }
                    }

                } else {

                }

            }

        })
    }


}