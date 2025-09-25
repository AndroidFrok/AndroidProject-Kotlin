package com.hjq.demo.ui.activity

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Environment
import android.os.IBinder
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.hjq.demo.R
import com.hjq.demo.app.AppActivity
import com.hjq.demo.services.VideoRecordService
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

class RecordAct : AppActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_record
    }

    override fun initView() {
        // 检查权限
        checkPermissions()

        // 绑定服务
        Intent(this, VideoRecordService::class.java).also { intent ->
            bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        }

        // 开始录制按钮
        findViewById<Button>(R.id.btnStart)?.setOnClickListener {
            XXPermissions.with(this).permission(Permission.CAMERA,Permission.WRITE_EXTERNAL_STORAGE).request(object :
                OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    val outputDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES)!!
                    recordService?.startRecording(outputDir)
                }
            })

        }

        // 停止录制按钮
        findViewById<Button>(R.id.btnStop)?.setOnClickListener {
            recordService?.stopRecording()
        }
    }

    override fun initData() {

    }

    private var recordService: VideoRecordService? = null
    private var isBound = false
    private val REQUEST_PERMISSIONS = 101

    // 服务连接回调
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as VideoRecordService.LocalBinder
            recordService = binder.service
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    // 检查并请求必要权限
    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val needPermissions = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (needPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this, needPermissions.toTypedArray(), REQUEST_PERMISSIONS
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }
}