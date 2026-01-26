package com.hjq.demo.services

import android.content.Context
import android.net.TrafficStats
import android.text.format.Formatter

object TrafficMonitor {
    private var lastRxBytes = 0L
    private var lastTxBytes = 0L
    private var appUid = 0

    fun init(context: Context) {
        appUid = context.applicationInfo.uid
        resetStats()
    }

    fun resetStats() {
        lastRxBytes = TrafficStats.getUidRxBytes(appUid)
        lastTxBytes = TrafficStats.getUidTxBytes(appUid)
    }

    fun getCurrentSessionTraffic(context: Context): String {
        val currentRx = TrafficStats.getUidRxBytes(appUid) - lastRxBytes
        val currentTx = TrafficStats.getUidTxBytes(appUid) - lastTxBytes
        return "接收: ${Formatter.formatFileSize(context, currentRx)}  发送: ${Formatter.formatFileSize(context, currentTx)}"
    }
}
