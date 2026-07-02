package com.hjq.demo.ui.fragment

import android.view.View
import com.hjq.demo.R
import com.hjq.demo.app.AppFragment
import com.hjq.demo.ui.activity.CopyActivity

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/10/18
 *    desc   : 可进行拷贝的副本
 */
class HomeFragment : AppFragment<CopyActivity>() {

    companion object {

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.home_fragment
    }

    override fun initView() {}

    override fun initData() {}

}