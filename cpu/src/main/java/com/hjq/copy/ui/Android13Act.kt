package com.hjq.copy.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.NotificationCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.button.MaterialButton
import com.hjq.copy.R
import com.hjq.demo.app.AppActivity
import com.hjq.demo.manager.Router
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import com.kongzue.dialogx.dialogs.PopTip

/**
 *  适配安卓13的调试界面
 */
@Route(path = Router.A13)
class Android13Act : AppActivity() {
    private val btn_notification: MaterialButton? by lazy { findViewById(R.id.btn_notification) }
    override fun getLayoutId(): Int {
        return R.layout.act_a13;
    }

    override fun initView() {
        btn_notification?.setOnClickListener {
            permissionNotification()
        }
    }

    override fun initData() {

    }

    private fun permissionNotification() {
        XXPermissions.with(this).permission(
            Permission.NOTIFICATION_SERVICE,
//            Permission.ACCESS_NOTIFICATION_POLICY,
            Permission.POST_NOTIFICATIONS
        )

            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    PopTip.show("OK").iconSuccess();
                    makeNoti();
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    super.onDenied(permissions, doNotAskAgain)
                    ToastUtils.show("权限申请失败")
//                    permissionNotification()
                }

            })
    }

    private fun makeNoti() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            val notificationId = getContext().applicationInfo.uid
            var channelId = ""
            // 适配 Android 8.0 通知渠道新特性
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    getString(com.hjq.demo.R.string.update_notification_channel_id),
                    getString(com.hjq.demo.R.string.update_notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW
                )
                channel.enableLights(false)
                channel.enableVibration(false)
                channel.vibrationPattern = longArrayOf(0)
                channel.setSound(null, null)
                notificationManager.createNotificationChannel(channel)
                channelId = channel.id
            }
            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(getContext(), channelId)
                    // 设置通知时间
                    .setWhen(System.currentTimeMillis())
                    // 设置通知标题
                    .setContentTitle(getString(com.hjq.demo.R.string.app_name))
                    // 设置通知小图标
                    .setSmallIcon(com.hjq.demo.R.mipmap.launcher_ic)
                    // 设置通知大图标
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            getResources(), com.hjq.demo.R.mipmap.launcher_ic
                        )
                    )
                    // 设置通知静音
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    // 设置震动频率
                    .setVibrate(longArrayOf(0))
                    // 设置声音文件
                    .setSound(null)
                    // 设置通知的优先级
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            notificationManager.notify(
                notificationId, notificationBuilder
                    // 设置通知的文本
                    .setContentText(
                        String.format(
                            "我是一个通知"
                        )
                    )
                    // 设置下载的进度
                    .setProgress(100, 23, false)
                    // 设置点击通知后是否自动消失
                    .setAutoCancel(true)
                    // 是否正在交互中
                    .setOngoing(true)
                    // 重新创建新的通知对象
                    .build()


            )
            postDelayed({ notificationManager.cancelAll() }, 10000)
        } else {
            PopTip.show("未开发").iconWarning()
        }

    }
}