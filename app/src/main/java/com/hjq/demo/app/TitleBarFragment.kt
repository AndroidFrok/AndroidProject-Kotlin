package com.hjq.demo.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.hjq.bar.TitleBar
import com.hjq.demo.action.TitleBarAction

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2020/10/31
 *    desc   : 带标题栏的 Fragment 业务基类
 */
abstract class TitleBarFragment<A : AppActivity> : AppFragment<A>()  {

    /** 标题栏对象 */
    private var titleBar: TitleBar? = null

    /** 状态栏沉浸 */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val titleBar = getTitleBar()
        // 设置标题栏点击监听
//        titleBar?.setOnTitleBarListener(this)

        if (isStatusBarEnabled()) {
            // 初始化沉浸式状态栏

            if (titleBar != null) {
                // 设置标题栏沉浸
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isStatusBarEnabled()) {
            // 重新初始化状态栏

        }
    }

    /**
     * 是否在 Fragment 使用沉浸式
     */
    open fun isStatusBarEnabled(): Boolean {
        return false
    }

    /**
     * 获取状态栏沉浸的配置对象
     */


    /**
     * 初始化沉浸式
     */
    /**
     * 获取状态栏字体颜色
     */
    protected open fun isStatusBarDarkFont(): Boolean {
        // 返回真表示黑色字体
        return getAttachActivity()!!.isStatusBarDarkFont()
    }

    /*override fun getTitleBar(): TitleBar? {
        if (titleBar == null || !isLoading()) {
            titleBar = obtainTitleBar(view as ViewGroup)
        }
        return titleBar
    }*/
}