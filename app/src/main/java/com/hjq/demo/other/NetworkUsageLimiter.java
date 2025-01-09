package com.hjq.demo.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUsageLimiter {

    private Context context;
    private long timeLimit; // 时间限制，例如30分钟

    public NetworkUsageLimiter(Context context, long timeLimit) {
        this.context = context;
        this.timeLimit = timeLimit;

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnected()) {
                    // 网络连接变为有效，开始计时
                    // TODO: 开始计时逻辑

                }
            }
        }
    };

    // 停止监听网络变化
    public void stopListening() {
        context.unregisterReceiver(receiver);
    }

}
