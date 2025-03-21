package com.hjq.copy.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
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
import timber.log.Timber

/**
 *  适配安卓13的调试界面
 */
@Route(path = Router.A13)
class Android13Act : AppActivity() {
    private val btn_notification: MaterialButton? by lazy { findViewById(R.id.btn_notification) }
    private val btn_nearbywifi: MaterialButton? by lazy { findViewById(R.id.btn_nearbywifi) }
    override fun getLayoutId(): Int {
        return R.layout.act_a13;
    }

    override fun initView() {
        btn_notification?.setOnClickListener {
            permissionNotification()
        }
        btn_nearbywifi?.setOnClickListener {
            this.checkWifiPermission();
        }
    }

    override fun initData() {

    }

    private fun checkWifiPermission() {
        val requiredPermissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.add(Manifest.permission.NEARBY_WIFI_DEVICES);
        }
        if (requiredPermissions.isEmpty()) {
            startWifiScan();
        } else {
            XXPermissions.with(this).permission(requiredPermissions)
                .request(object : OnPermissionCallback {
                    override fun onGranted(p0: MutableList<String>, p1: Boolean) {
                        PopTip.show("OK").iconSuccess();
                        startWifiScan();
                    }

                    override fun onDenied(
                        permissions: MutableList<String>, doNotAskAgain: Boolean
                    ) {
                        super.onDenied(permissions, doNotAskAgain)
                        PopTip.show("权限申请失败").iconWarning();
                    }
                })
        }


    }

    private val wifiManager by lazy {
        applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private fun startWifiScan() {
        if (wifiManager.isWifiEnabled) {
            val success = wifiManager.startScan()
            if (!success) {
                // 处理扫描失败
                PopTip.show("扫描失败").iconWarning();
                Timber.e("WifiScan Failed to start Wi-Fi scan")
            } else {
                PopTip.show("扫描中").iconSuccess();
            }
        } else {
            // 提示用户打开 Wi-Fi
            Toast.makeText(this, "请开启 Wi-Fi", Toast.LENGTH_SHORT).show()
        }
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


    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                if (XXPermissions.isGranted(
                        getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    if (ActivityCompat.checkSelfPermission(
                            getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Timber.e("错 WifiScan Failed to get scan results");
                        return
                    }
                    val scanResults = wifiManager.scanResults
                    processScanResults(scanResults)
                }

            }
        }
    }

    // 注册接收器
    private fun registerWifiReceiver() {
        val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)
    }

    // 取消注册（在 onDestroy 中调用）
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiScanReceiver)
    }

    private fun processScanResults(results: List<ScanResult>) {
        val wifiList = results.map { scanResult ->
            Timber.i("scanResult ssid = ${scanResult.SSID}")/*WifiInfo(
                ssid = scanResult.SSID,
                bssid = scanResult.BSSID,
                signalStrength = WifiManager.calculateSignalLevel(scanResult.level, 5),
                capabilities = scanResult.capabilities
            )*/
        }
        // 更新 UI 或处理数据
        Timber.d("WifiScan Found ${wifiList.size} networks")
    }
}