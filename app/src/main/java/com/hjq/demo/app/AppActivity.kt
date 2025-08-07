package com.hjq.demo.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import com.hjq.bar.TitleBar
import com.hjq.base.BaseActivity
import com.hjq.base.BaseDialog
import com.hjq.demo.R
import com.hjq.demo.action.TitleBarAction
import com.hjq.demo.action.ToastAction
import com.hjq.demo.http.model.HttpData
import com.hjq.demo.other.AppConfig
import com.hjq.demo.ui.dialog.WaitDialog
import com.hjq.http.listener.OnHttpListener
import com.hjq.language.MultiLanguages
import okhttp3.Call

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/10/18
 *    desc   : Activity 业务基类
 */
abstract class AppActivity : BaseActivity(), ToastAction, TitleBarAction, OnHttpListener<Any> {
//    protected var serialHelper: SerialHelper? = null

    /** 标题栏对象 */
    private var titleBar: TitleBar? = null

    /** 加载对话框 */
    private var dialog: BaseDialog? = null

    /** 对话框数量 */
    private var dialogCount: Int = 0

    /**
     * 当前加载对话框是否在显示中
     */
    open fun isShowDialog(): Boolean {
        return dialog != null && dialog!!.isShowing
    }

    /**
     * 显示加载对话框
     */
    open fun showDialog() {
        if (isFinishing || isDestroyed) {
            return
        }
        dialogCount++
        postDelayed(Runnable {
            if ((dialogCount <= 0) || isFinishing || isDestroyed) {
                return@Runnable
            }
            if (dialog == null) {
                dialog = WaitDialog.Builder(this).setCancelable(false).create()
            }
            if (!dialog!!.isShowing) {
//                dialog!!.show()
            }
        }, 300)
    }

    /**
     * 隐藏加载对话框
     */
    open fun hideDialog() {
        if (isFinishing || isDestroyed) {
            return
        }
        if (dialogCount > 0) {
            dialogCount--
        }
        if ((dialogCount != 0) || (dialog == null) || !dialog!!.isShowing) {
            return
        }
        dialog?.dismiss()
    }

    override fun initLayout() {
        super.initLayout()

        val titleBar = getTitleBar()
        titleBar?.setOnTitleBarListener(this)

        // 初始化沉浸式状态栏
        if (isStatusBarEnabled()) {
            // 设置标题栏沉浸
            if (titleBar != null) {
//                ImmersionBar.setTitleBar(this, titleBar)
            }
        }
    }

    /**
     * 是否使用沉浸式状态栏
     */
    protected open fun isStatusBarEnabled(): Boolean {
        return true
    }

    /**
     * 状态栏字体深色模式
     */
    open fun isStatusBarDarkFont(): Boolean {
        return true
    }

    /**
     * 设置标题栏的标题
     */
    override fun setTitle(@StringRes id: Int) {
        title = getString(id)
    }

    /**
     * 设置标题栏的标题
     */
    override fun setTitle(title: CharSequence?) {
        super<BaseActivity>.setTitle(title)
        getTitleBar()?.title = title
    }

    override fun getTitleBar(): TitleBar? {
        if (titleBar == null) {
            titleBar = obtainTitleBar(getContentView())
        }
        return titleBar
    }

    override fun onLeftClick(titleBar: TitleBar?) {
        onBackPressed()
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        super.startActivityForResult(intent, requestCode, options)
        overridePendingTransition(R.anim.right_in_activity, R.anim.right_out_activity)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.left_in_activity, R.anim.left_out_activity)
    }

    /**
     * [OnHttpListener]
     */
    override fun onStart(call: Call) {
        showDialog()
    }

    override fun onSucceed(result: Any) {
        if (result is HttpData<*>) {
            toast(result.getMessage())
        }
    }

    override fun onFail(e: Exception) {
//        toast(e.message)
        toast("离线模式")
    }

    override fun onEnd(call: Call) {
        hideDialog()
    }

    override fun onResume() {
        super.onResume()
        if (AppConfig.isDebug()) {
            getTitleBar()?.setTitle(localClassName + "-" + getTitleBar()?.title)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isShowDialog()) {
            hideDialog()
        }
        dialog = null
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(MultiLanguages.attach(newBase))
    }
}