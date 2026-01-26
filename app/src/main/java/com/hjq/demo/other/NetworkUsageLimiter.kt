package com.hjq.demo.other

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo

class NetworkUsageLimiter(private val context: Context, private val timeLimit: Long) {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = cm.activeNetworkInfo
                if (activeNetwork != null && activeNetwork.isConnected) {
                    // 网络连接变为有效，开始计时
                    // TODO: 开始计时逻辑
                }
            }
        }
    }

    init {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(receiver, filter)
    }

    fun stopListening() {
        context.unregisterReceiver(receiver)
    }
}
