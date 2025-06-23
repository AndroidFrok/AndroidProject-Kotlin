package com.hjq.demo.services;

import android.content.Context;
import android.net.TrafficStats;
import android.text.format.Formatter;

public class TrafficMonitor {
    private static long lastRxBytes = 0;
    private static long lastTxBytes = 0;
    private static int appUid;

    public static void init(Context context) {
        appUid = context.getApplicationInfo().uid;
        resetStats();
    }

    public static void resetStats() {
        lastRxBytes = TrafficStats.getUidRxBytes(appUid);
        lastTxBytes = TrafficStats.getUidTxBytes(appUid);
    }

    // 获取当前会话使用的流量
    public static String getCurrentSessionTraffic(Context context) {
        long currentRx = TrafficStats.getUidRxBytes(appUid) - lastRxBytes;
        long currentTx = TrafficStats.getUidTxBytes(appUid) - lastTxBytes;
        return "接收: " + Formatter.formatFileSize(context, currentRx) + "  发送: " + Formatter.formatFileSize(context, currentTx);
    }
}
