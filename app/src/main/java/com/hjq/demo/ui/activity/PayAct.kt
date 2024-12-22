package com.hjq.demo.ui.activity

import android.view.View
import com.flyco.tablayout.SegmentTabLayout
import com.hjq.demo.R
import com.hjq.demo.app.AppActivity
import com.kyle.radiogrouplib.NestedRadioGroup
import timber.log.Timber

/**
 *   收银台 -- 支付页 2024年8月29日14:03:37
 */
class PayAct : AppActivity() {
    private lateinit var tabLayout_5: SegmentTabLayout;
    private val radio2: NestedRadioGroup? by lazy { findViewById(R.id.radio2) };

    //    val tabLayout_5: SegmentTabLayout? by lazy { findViewById(R.id.tl_3) }
    private lateinit var mDecorView: View;
//    private lateinit var mb:Mod
    override fun getLayoutId(): Int {
        return R.layout.layout_pay;
    }

    override fun initView() {
        mDecorView = window.decorView;
        tabLayout_5 = mDecorView.findViewById(R.id.tl_5)

        radio2?.setOnCheckedChangeListener { group, checkedId ->
            Timber.d("setOnCheckedChangeListener $checkedId")
        }
    }

    override fun initData() {
        val tabData = arrayOf("普通购物", "送心意")
        tabLayout_5.setTabData(tabData)
    }
}