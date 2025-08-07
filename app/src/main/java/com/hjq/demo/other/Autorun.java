package com.hjq.demo.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hjq.demo.ui.activity.HomeActivity;
import com.hjq.demo.ui.activity.SplashActivity;
import com.hjq.toast.ToastUtils;
import com.kongzue.dialogx.dialogs.PopTip;

/**
 * 高版本安卓  需要 在act中动态注册了
 * val filterAutorun = IntentFilter();
 * filterAutorun.addAction(Intent.ACTION_BOOT_COMPLETED);
 * registerReceiver(Autorun(), filterAutorun)
 */
public class Autorun extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent1) {
        if (intent1.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (AppConfig.INSTANCE.isDebug() == false) {
                ToastUtils.show("开机了");
                Intent intent = new Intent();
                intent.setClass(context, SplashActivity.class);// 开机后指定要执行程序的界面文件
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                PopTip.show("调试模式下不进行自启");
            }

        }
    }
}
