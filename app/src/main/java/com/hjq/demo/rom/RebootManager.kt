package com.hjq.demo.other

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import java.util.*

/**
 *  用法
 *        RebootManager.setupDailyReboot(this);
 *        
 *        主界面需要申请权限
 *            <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
 *     <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
 *     <uses-permission android:name="android.permission.WAKE_LOCK" />
 *
 *
 *                if (!XXPermissions.isGranted(this, Permission.SCHEDULE_EXACT_ALARM)) {
 *             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
 *                 val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
 *                     data = android.net.Uri.parse("package:${packageName}")
 *                 }
 *                 startActivity(intent)
 *             } else {
 *                 XXPermissions.with(this).permission(Permission.SCHEDULE_EXACT_ALARM)
 *                     .request(object : OnPermissionCallback {
 *                         override fun onGranted(p0: MutableList<String>, all: Boolean) {
 *
 *                         }
 *
 *                     })
 *             }
 *         }
 */

object RebootManager {

    // 重启广播的 Action
    const val ACTION_REBOOT = "com.example.ACTION_REBOOT"
    private const val REBOOT_INTERVAL = 3 * 60 * 1000L
    /**
     * 初始化每天凌晨3点的定时重启任务
     */
    fun setupDailyReboot(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RebootReceiver::class.java).apply {
            action = ACTION_REBOOT
        }

        // 创建 PendingIntent（使用 FLAG_IMMUTABLE 适配 Android 12+）
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val firstTriggerTime = System.currentTimeMillis() + REBOOT_INTERVAL
        // 设置每天凌晨3点执行
        /*val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 3) // 小时（24小时制）
            set(Calendar.MINUTE, 0)      // 分钟
            set(Calendar.SECOND, 0)      // 秒
            // 若当前时间已过今天3点，则设置为明天3点
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }*/

        // 设置重复触发（每3分钟一次）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //  Android 6.0+ 推荐使用 setExactAndAllowWhileIdle 保证精度
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, // 唤醒设备执行
                firstTriggerTime, //  AlarmManager.INTERVAL_DAY
                pendingIntent
            )
        } else {
            // 低版本使用 setRepeating
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                firstTriggerTime, // AlarmManager.INTERVAL_DAY
                REBOOT_INTERVAL,
                pendingIntent
            )
        }
    }

    /**
     * 执行重启操作（需系统权限或 root）
     */
    open fun rebootSystem(context: Context) {
        try {
            // 方式1：系统应用使用 PowerManager（推荐）
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.reboot(null) // 需要 REBOOT 权限

            // 方式2：root 设备执行 shell 命令（备选）
            // Runtime.getRuntime().exec("su -c reboot")
        } catch (e: Exception) {
            e.printStackTrace()
            // 重启失败（无权限或其他原因）
        }
    }
}
