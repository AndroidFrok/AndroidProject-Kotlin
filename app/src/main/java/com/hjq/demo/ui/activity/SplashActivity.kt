package com.hjq.demo.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.hjq.demo.R
import com.hjq.demo.app.AppActivity
import com.hjq.demo.manager.MmkvUtil
import com.hjq.demo.manager.ThreadPoolManager
import com.hjq.demo.other.AppConfig
import com.hjq.demo.other.AppConfig.getHostUrl
import com.hjq.widget.view.SlantedTextView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.concurrent.thread

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/10/18
 *    desc   : 闪屏界面
 */
class SplashActivity : AppActivity() {

    private val lottieView: LottieAnimationView? by lazy { findViewById(R.id.lav_splash_lottie) }
    private val debugView: SlantedTextView? by lazy { findViewById(R.id.iv_splash_debug) }

    override fun getLayoutId(): Int {
        return R.layout.splash_activity
    }

    override fun initView() {
        /*GlobalScope.launch {

        }*/
//        ThreadPoolManager.getInstance().execute(r) // 用自己的闪屏  不用现有动画
        // 设置动画监听
//        lottieView?.speed = 8.6f;
        lottieView?.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                lottieView?.removeAnimatorListener(this)
                toMain()
            }
        })
    }

    private fun toMain() {
        val isAgreePrivacy = MmkvUtil.getBool("is_agree")  //这个需要用 datastore组件来维护
        if (isAgreePrivacy) {
            HomeActivity.start(this@SplashActivity)
        } else {
//                     跳转到隐私政策页
            BrowserActivity.start(getContext(), "http://sdzxkc.com/newsInfo-94-8.html")
        }

        finish()
    }

    override fun initData() {
        debugView?.let {
            it.setText(AppConfig.getBuildType().uppercase(Locale.getDefault()))
            if (AppConfig.isDebug()) {
                it.visibility = View.VISIBLE
            } else {
                it.visibility = View.INVISIBLE
            }
        }
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig()
            // 隐藏状态栏和导航栏
            .hideBar(BarHide.FLAG_HIDE_BAR)
    }

    override fun onBackPressed() {
        // 禁用返回键
        //super.onBackPressed();
    }

    override fun initActivity() {
        // 问题及方案：https://www.cnblogs.com/net168/p/5722752.html
        // 如果当前 Activity 不是任务栈中的第一个 Activity
        if (!isTaskRoot) {
            val intent: Intent? = intent
            // 如果当前 Activity 是通过桌面图标启动进入的
            if (((intent != null) && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && (Intent.ACTION_MAIN == intent.action))) {
                // 对当前 Activity 执行销毁操作，避免重复实例化入口
                finish()
                return
            }
        }
        super.initActivity()
    }

    override fun onDestroy() {
        // 因为修复了一个启动页被重复启动的问题，所以有可能 Activity 还没有初始化完成就已经销毁了
        // 所以如果需要在此处释放对象资源需要先对这个对象进行判空，否则可能会导致空指针异常
        super.onDestroy()
    }

    var r = Runnable {

//             如果想用自己的闪屏  不用现有动画 可打开延迟注释  并隐藏xml里的控件 android:visibility="gone"
        /*try {
                  Thread.sleep(1000);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }*/


//        toMain()
        val u = getHostUrl()
//        u = "#";
        BrowserActivity.start(getContext(), u)
        finish()
    }
}