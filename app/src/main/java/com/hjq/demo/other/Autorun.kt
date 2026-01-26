package com.hjq.demo.other

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hjq.demo.ui.activity.SplashActivity
import com.hjq.toast.ToastUtils
import com.kongzue.dialogx.dialogs.PopTip

/**
 * 高版本安卓  需要 在act中动态注册了
 * val filterAutorun = IntentFilter();
 * filterAutorun.addAction(Intent.ACTION_BOOT_COMPLETED);
 * registerReceiver(Autorun(), filterAutorun)
 */
class Autorun : BroadcastReceiver() {
    override fun onReceive(context: Context, intent1: Intent) {
        if (intent1.action == "android.intent.action.BOOT_COMPLETED") {
            if (!AppConfig.isDebug) {
                ToastUtils.show("开机了")
                val intent = Intent()
                intent.setClass(context, SplashActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } else {
                PopTip.show("调试模式下不进行自启")
            }
        }
    }
}
