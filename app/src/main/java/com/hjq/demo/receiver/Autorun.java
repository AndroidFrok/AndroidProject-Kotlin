package com.hjq.demo.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.hjq.demo.ui.activity.HomeActivity;
import com.hjq.demo.ui.activity.SplashActivity;

public class Autorun extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent1) {
        if (intent1.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            int flag = Intent.FLAG_ACTIVITY_NEW_TASK;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                flag = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
            }
            Intent intent = new Intent();
            intent.setClass(context, SplashActivity.class);// 开机后指定要执行程序的界面文件
            intent.addFlags(flag);
            context.startActivity(intent);
        }
    }
}
