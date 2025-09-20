package com.hjq.demo.other

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.io.IOException

class RebootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == RebootManager.ACTION_REBOOT) {
            // 接收到广播后执行重启
//            RebootReceiver.rebootSystem(context)
            rebootDevice();
        }
    }

    /**
     * https://blog.csdn.net/wzystal/article/details/26088987
     */
    private fun rebootDevice() {
//        方案 1
        /*val reboot = Intent(Intent.ACTION_REBOOT)

        sendBroadcast(reboot)*/
//        方案 2
        val cmd = "adb reboot";
        try {
            Runtime.getRuntime().exec(cmd)
        } catch (e: IOException) {
            e.printStackTrace()
        }

//        方案 3
        /*val pManager = getSystemService(POWER_SERVICE) as PowerManager
        pManager.reboot("重启")*/

//        4
        /*try {

        }*/
    }
}
