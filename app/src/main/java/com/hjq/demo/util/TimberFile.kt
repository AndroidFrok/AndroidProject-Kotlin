package com.hjq.demo.util

import android.content.Context
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Timber 日志文件树，将日志写入外置存储器
 * 路径: /storage/emulated/0/zhibingji_logs/月_x日_x.txt
 */
class TimberFile(ctx: Context) : Timber.Tree() {
    private val context = ctx

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        try {
            val calendar = Calendar.getInstance()
            val month = calendar.get(Calendar.MONTH) + 1
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            val fileName = "月_$month" + "日_$dayOfMonth.txt"

            // 获取日志目录（优先外置存储，失败则回退到内部存储）
            val logDir = LogUploadUtil.getLogPath(context)
            val logFile = File(logDir, fileName)

            // 追加日志内容
            val logContent = getFormatDateTime("yyyy/MM/dd HH:mm:ss-SSS0") + " -$tag- $message \n"
            logFile.appendText(logContent)

        } catch (e: Exception) {
            // 静默处理，避免日志写入失败导致应用崩溃
            e.printStackTrace()
        }


    }

    fun getFormatDateTime(format: String?): String {
        var format = format
        if (format == null || format.isEmpty()) {
            format = "yyyyMMdd_HHmmss" //  默认适配文件名   不加标点 符号
        }
        return SimpleDateFormat(format, Locale.getDefault()).format(Date())
    }
}
