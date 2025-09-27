package com.hjq.demo.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.hjq.demo.R
import com.hjq.toast.ToastUtils
import com.kongzue.dialogx.dialogs.PopTip
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class VideoRecordService : Service() {
    private val TAG = "VideoRecordService"
    private val binder = LocalBinder()

    // 摄像头相关
    private var cameraManager: CameraManager? = null
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var cameraId: String? = null
    private val cameraOpenCloseLock = Semaphore(1)

    // 录制相关
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var videoFile: File? = null

    // 唤醒锁
    private var wakeLock: PowerManager.WakeLock? = null

    inner class LocalBinder : Binder() {
        val service: VideoRecordService
            get() = this@VideoRecordService
    }

    override fun onBind(intent: Intent): IBinder = binder

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate() {
        super.onCreate()
        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        acquireWakeLock()
        createNotificationChannel()
    }

    /**
     * 开始录制视频
     * @param outputDir 视频保存目录
     * @return 录制的视频文件
     */
    fun startRecording(outputDir: File): File? {
        if (isRecording) return null

        // 创建输出文件
        val fileName = "REC_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.mp4"
        videoFile = File(outputDir, fileName)

        // 初始化MediaRecorder
        if (!initMediaRecorder(videoFile!!)) {
            Log.e(TAG, "初始化MediaRecorder失败")
            return null
        }

        // 打开摄像头并开始录制
        startCamera()
        isRecording = true
        startForeground(NOTIFICATION_ID, createNotification())
        return videoFile
    }

    /**
     * 停止录制视频
     */
    fun stopRecording() {
        if (!isRecording) return
        isRecording = false
        try {
            // 停止录制
            mediaRecorder?.stop()
            mediaRecorder?.reset()
        } catch (e: Exception) {
            Log.e(TAG, "停止录制失败: ${e.localizedMessage}")
            ToastUtils.show("停止失败：${e.localizedMessage}")
        }

        // 释放摄像头资源
        closeCamera()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    /**
     * 初始化MediaRecorder
     */
    private fun initMediaRecorder(outputFile: File): Boolean {
        return try {
            mediaRecorder = MediaRecorder().apply {
                // 设置音频源
                setAudioSource(MediaRecorder.AudioSource.MIC)
                // 设置视频源 (Camera2不需要在这里设置，而是通过Surface关联)
                setVideoSource(MediaRecorder.VideoSource.SURFACE)

                // 设置输出格式和编码
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)

                // 设置视频参数
                setVideoSize(1920, 1080) // 1080p
                setVideoFrameRate(30)
                setVideoEncodingBitRate(10 * 1024 * 1024) // 10Mbps
                setAudioEncodingBitRate(128000) // 128kbps
                setAudioSamplingRate(44100)

                // 设置方向
                setOrientationHint(90)

                // 设置输出文件
                setOutputFile(outputFile.absolutePath)

                // 准备录制
                prepare()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace();
            Log.e(TAG, "初始化MediaRecorder失败: ${e.message}", e)
            mediaRecorder?.release()
            mediaRecorder = null
            false
        }
    }

    /**
     * 打开摄像头并开始录制
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startCamera() {
        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("获取摄像头锁超时")
            }

            // 获取可用摄像头列表
            val cameraIds = cameraManager?.cameraIdList ?: return

            // 优先选择后置摄像头
            cameraId = cameraIds.firstOrNull {
                val characteristics = cameraManager?.getCameraCharacteristics(it)
                val facing = characteristics?.get(CameraCharacteristics.LENS_FACING)
                facing == CameraCharacteristics.LENS_FACING_BACK
            } ?: cameraIds[0] // 没有后置则选第一个可用摄像头
//            cameraId = "5" ;
            // 检查权限并打开摄像头
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                cameraManager?.openCamera(cameraId!!, stateCallback, null)
                PopTip.show("摄像头打开成功").iconSuccess();
            }
        } catch (e: Exception) {
            e.printStackTrace();
//            Log.e(TAG, "打开摄像头失败: ${e.message}", e)
//            ToastUtils.show("打开摄像头失败：${e.localizedMessage}")
            PopTip.show("摄像头打开失败 ${e.localizedMessage}").iconError();
        }
    }

    /**
     * 关闭摄像头
     */
    private fun closeCamera() {
        try {
            cameraOpenCloseLock.acquire()
            captureSession?.close()
            captureSession = null
            cameraDevice?.close()
            cameraDevice = null
            mediaRecorder?.release()
            mediaRecorder = null
        } catch (e: InterruptedException) {
            throw RuntimeException("关闭摄像头时被中断: ${e.message}", e)
        } finally {
            cameraOpenCloseLock.release()
        }
    }

    /**
     * 摄像头状态回调
     */
    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraOpenCloseLock.release()
            cameraDevice = camera
            // 打开摄像头后，创建捕获会话开始录制
            createCaptureSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraOpenCloseLock.release()
            camera.close()
            cameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraOpenCloseLock.release()
            camera.close()
            cameraDevice = null
            Log.e(TAG, "摄像头错误: $error")
        }
    }

    /**
     * 创建捕获会话
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createCaptureSession() {
        try {
            // 获取MediaRecorder的Surface
            val surface = mediaRecorder?.surface ?: return

            // 创建捕获请求
            val captureRequestBuilder = cameraDevice?.createCaptureRequest(
                CameraDevice.TEMPLATE_RECORD
            ) ?: return
            captureRequestBuilder.addTarget(surface)

            // 创建捕获会话
            cameraDevice?.createCaptureSession(
                listOf(surface), // 无预览，只需要MediaRecorder的Surface
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        if (cameraDevice == null) return

                        captureSession = session
                        try {
                            // 设置重复请求开始录制
                            val captureRequest = captureRequestBuilder.build()
                            session.setRepeatingRequest(
                                captureRequest, null, null
                            )
                        } catch (e: CameraAccessException) {
                            Log.e(TAG, "创建捕获请求失败: ${e.message}", e)
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e(TAG, "创建捕获会话失败 ")
                    }
                }, null
            )
        } catch (e: Exception) {
            Log.e(TAG, "创建捕获会话出错: ${e.message}", e)
        }
    }

    /**
     * 获取唤醒锁，防止设备休眠
     */
    private fun acquireWakeLock() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK, "VideoRecord:WakeLock"
        ).apply {
            acquire(10 * 60 * 1000L) // 10分钟超时
        }
    }

    /**
     * 创建通知渠道
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "视频录制服务", NotificationManager.IMPORTANCE_LOW
            ).apply {
                setSound(null, null) // 无提示音
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 创建前台服务通知
     */
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("正在录制视频")
            .setSmallIcon(R.mipmap.add).setPriority(NotificationCompat.PRIORITY_LOW).build()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
        wakeLock?.release()
    }

    companion object {
        private const val NOTIFICATION_ID = 10086
        private const val CHANNEL_ID = "video_record_channel"
    }
}
