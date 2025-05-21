package com.hjq.demo.manager;

import android.os.CountDownTimer;

import timber.log.Timber;
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
 * //                    Timber.d("onTick " + millisUntilFinished)
 * }
 * <p>
 * override fun onFinish() {
 * setRemainSeconds(0)
 * }
 * })
 * <p>
 * }
 */
public class CountDownManager {

    private static CountDownManager instance;
    private CountDownTimer currentTimer;

    public static synchronized CountDownManager getInstance() {
        if (instance == null) {
            instance = new CountDownManager();
        }
        return instance;
    }

    public void scheduleTimer(long duration, TimerCallback callback) {
        cancelCurrent();
        currentTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                callback.onTick(millisUntilFinished);
                Timber.d("onTick1: %s", millisUntilFinished);
            }

            @Override
            public void onFinish() {
                callback.onFinish();
            }

            // 通过callback接口传递事件
        }.start();
    }

    public void cancelCurrent() {
        if (currentTimer != null) {
            currentTimer.cancel();
            currentTimer = null;
        }
    }

    public interface TimerCallback {
        void onTick(long millisUntilFinished);

        void onFinish();
    }
}
