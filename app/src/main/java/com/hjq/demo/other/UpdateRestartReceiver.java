package com.hjq.demo.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hjq.demo.ui.activity.HomeActivity;

public class UpdateRestartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 检查广播的类型
        if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            // 启动应用的主活动
            Intent launchIntent = new Intent(context, HomeActivity.class);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
            // 显示 Toast 消息
            Toast.makeText(context, "本机应用已升级", Toast.LENGTH_LONG).show();
        }

    }
}
