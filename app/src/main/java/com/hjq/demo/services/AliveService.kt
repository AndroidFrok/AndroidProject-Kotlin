package com.hjq.demo.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.hjq.demo.manager.ActivityManager
import com.hjq.demo.manager.RootCmd
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask

class AliveService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null;
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                val isF = RootCmd.isAppForeground(applicationContext, packageName)
                if (!isF) {
                    Timber.e("处于后台  $isF")
                    ActivityManager.getInstance().finishAllActivities();
//                    startActivity(Intent(applicationContext, MainAct::class.java))
                }
            }

        };
        timer.schedule(task, 1000, 5 * 60 * 1000)
        return super.onStartCommand(intent, flags, startId)
    }
}