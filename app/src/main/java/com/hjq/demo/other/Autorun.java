package com.hjq.demo.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hjq.demo.ui.activity.HomeActivity;

public class Autorun extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent1) {
        if (intent1.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent intent = new Intent();
            intent.setClass(context, HomeActivity.class);// 开机后指定要执行程序的界面文件
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
