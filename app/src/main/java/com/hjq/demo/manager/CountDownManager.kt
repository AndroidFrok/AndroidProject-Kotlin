package com.hjq.demo.manager

import android.os.CountDownTimer
import timber.log.Timber

/**
 * 倒计时 单例管理  防止 同一个界面重新计时 的时候出现多个重复的计时
 * 调用示例
 * private fun remainTimeCountDown(minutes: Long) {
 * CountDownManager.getInstance()
 * .scheduleTimer(minutes, object : CountDownManager.TimerCallback {
 * override fun onTick(millisUntilFinished: Long) {
 * val t = millisUntilFinished / 1000;
 * Timber.i("倒计时 $t");
 * setRemainSeconds(t.toInt())
 * }
 *
 * override fun onFinish() {
 * setRemainSeconds(0)
 * }
 * })
 * }
 */
class CountDownManager private constructor() {

    private var currentTimer: CountDownTimer? = null

    fun scheduleTimer(duration: Long, callback: TimerCallback) {
        cancelCurrent()
        currentTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                callback.onTick(millisUntilFinished)
                Timber.d("onTick1: %s", millisUntilFinished)
            }

            override fun onFinish() {
                callback.onFinish()
            }
        }.start()
    }

    fun cancelCurrent() {
        currentTimer?.cancel()
        currentTimer = null
    }

    interface TimerCallback {
        fun onTick(millisUntilFinished: Long)
        fun onFinish()
    }

    companion object {
        @Volatile
        private var instance: CountDownManager? = null

        @JvmStatic
        fun getInstance(): CountDownManager = synchronized(this) {
            instance ?: CountDownManager().also { instance = it }
        }
    }
}
