package com.hjq.demo.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.hardware.Camera
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.hjq.demo.R
import com.hjq.toast.ToastUtils
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VideoRecordService2 : Service() {
    private val TAG = "VideoRecordService"
    private val binder = LocalBinder()
    private var mediaRecorder: MediaRecorder? = null
    private var camera: Camera? = null
    private var isRecording = false
    private var wakeLock: PowerManager.WakeLock? = null // 保持设备唤醒

    // 绑定服务的Binder
    inner class LocalBinder : Binder() {
        val service: VideoRecordService2
            get() = this@VideoRecordService2
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        // 获取唤醒锁（避免设备休眠导致录制中断）
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "VideoRecord:WakeLock"
        ).apply {
            acquire(10 * 60 * 1000L /* 10分钟超时 */)
        }
    }

    private var cameraParameters: Camera.Parameters? = null

    /**
     * 开始录制视频
     * @param outputDir 视频保存目录
     * @return 录制的视频文件
     */
    fun startRecording(outputDir: File): File {
        if (isRecording) return File("")

        // 创建输出文件
        val fileName = "REC_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.mp4"
        val outputFile = File(outputDir, fileName)

        try {
            // 1. 初始化摄像头（无预览）
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT).apply {
                setDisplayOrientation(90) // 调整方向
                // 关键：绑定空Surface（无预览）
                cameraParameters = parameters
                cameraParameters?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
                this.parameters = parameters
                unlock() // 解锁摄像头供MediaRecorder使用

            }

            // 2. 初始化MediaRecorder
            mediaRecorder = MediaRecorder().apply {
                // 设置摄像头
                setCamera(camera)

                // 音频源和视频源
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setVideoSource(MediaRecorder.VideoSource.CAMERA)
//                mediaRecorder?.setAudioEncodingBitRate(128000) // 128kbps（设备普遍支持）
                // 输出格式和编码
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
// 使用时选择合适的分辨率（如选择接近 720P 的支持分辨率）

                val targetSize =
                    cameraParameters?.supportedVideoSizes?.firstOrNull { it.width == 1280 && it.height == 720 }
                        ?: cameraParameters?.supportedVideoSizes!![0] // 无 720P 时用第一个支持的尺寸

                // 视频参数（分辨率、帧率、比特率）
                setVideoSize(targetSize.width, targetSize.height) // 720P
                setVideoFrameRate(30)
                setVideoEncodingBitRate(2 * 1024 * 1024) // 2Mbps

                // 方向调整
                setOrientationHint(90)

                // 输出文件
                setOutputFile(outputFile.absolutePath)

                // 准备录制
                kotlin.runCatching {
                    prepare();
                    start();
                }.onFailure {
                    ToastUtils.show("准备录制失败")
                    Timber.w("准备录制失败：${it.localizedMessage}")
                }.onSuccess {
                    ToastUtils.show("开始录制");
                    isRecording = true
                    // 启动前台服务（必须，否则Android 10+会报错）
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForeground(NOTIFICATION_ID, createNotification())
                    }
                    Log.d(TAG, "开始录制：${outputFile.absolutePath}")
                }.recover {
                    Timber.w("recover：${it.localizedMessage}")

                }

            }


            return outputFile
        } catch (e: Exception) {
            Log.e(TAG, "录制失败：${e.message}", e)
            stopRecording()
            return File("")
        }
    }

    // 示例：通过 Camera API 获取支持的视频分辨率
    private fun getSupportedVideoSizes(camera: Camera): List<Camera.Size> {
        val parameters = camera.parameters
        // 获取设备支持的视频录制分辨率（部分设备用 supportedPreviewSizes）
        return parameters.supportedVideoSizes ?: parameters.supportedPreviewSizes
    }

    /**
     * 停止录制
     */
    fun stopRecording() {
        if (!isRecording) return

        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            camera?.lock()
            camera?.release()
        } catch (e: Exception) {
            Log.e(TAG, "停止录制失败：${e.message}", e)
            ToastUtils.show("停止失败：${e.message}")
        } finally {
            mediaRecorder = null
            camera = null
            isRecording = false
            stopForeground(STOP_FOREGROUND_REMOVE)
            Log.d(TAG, "停止录制")
        }
    }

    /**
     * 创建前台服务通知（Android 8.0+ 必须）
     */
    private fun createNotification(): Notification {
        val channelId = "video_record_channel"
        // 创建通知渠道
        val channel = NotificationChannel(
            channelId,
            "视频录制服务",
            NotificationManager.IMPORTANCE_LOW // 低优先级，减少打扰
        ).apply {
            setSound(null, null) // 无提示音
        }
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            channel
        )

        // 构建通知（可隐藏在状态栏，仅显示小图标）
        return Notification.Builder(this, channelId)
            .setContentTitle("正在录制视频")
            .setSmallIcon(R.mipmap.launcher_ic) // 替换为透明图标可实现"无感知"
            .setPriority(Notification.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
        wakeLock?.release() // 释放唤醒锁
    }

    companion object {
        private const val NOTIFICATION_ID = 10086
    }
}
