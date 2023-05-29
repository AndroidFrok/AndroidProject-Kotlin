package com.hjq.demo.ui.fragment

import android.os.SystemClock
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.ShellUtils
import com.hjq.base.BaseAdapter
import com.hjq.demo.R
import com.hjq.demo.app.AppActivity
import com.hjq.demo.app.TitleBarFragment
import com.hjq.demo.ui.activity.RestartActivity
import com.hjq.demo.ui.adapter.StatusAdapter
import com.hjq.toast.ToastUtils
import com.hjq.widget.layout.WrapRecyclerView
import com.kongzue.dialogx.dialogs.PopTip
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import java.util.*

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2020/07/10
 *    desc   : 加载案例 Fragment
 */
class StatusFragment : TitleBarFragment<AppActivity>(), OnRefreshLoadMoreListener,
    BaseAdapter.OnItemClickListener {

    companion object {

        fun newInstance(): StatusFragment {
            return StatusFragment()
        }
    }

    private fun testInstall() {
        //            测试静默安装
        ResourceUtils.copyFileFromAssets(
            "SilentInstall-1.1.0-10.apk", "/sdcard/SilentInstall.apk"
        )
        SystemClock.sleep(2000)
        val result = ShellUtils.execCmd("pm install -r /sdcard/SilentInstall.apk", true)
        SystemClock.sleep(1000)
        if (result.result == 0) {
            if (result.successMsg.equals("Success")) {
//                  安装成功  重启app
                PopTip.show("静默更新成功")
                getAttachActivity()?.getContext()?.let { RestartActivity.restart(it) }
            }
        } else {
//            安装失败
            ToastUtils.show("安装失败")
            PopTip.show("安装失败")
        }
    }

    private val refreshLayout: SmartRefreshLayout? by lazy { findViewById(R.id.rl_status_refresh) }
    private val recyclerView: WrapRecyclerView? by lazy { findViewById(R.id.rv_status_list) }

    private var adapter: StatusAdapter? = null

    override fun getLayoutId(): Int {
        return R.layout.status_fragment
    }

    override fun initView() {
        adapter = StatusAdapter(getAttachActivity()!!)
        adapter?.setOnItemClickListener(this)

        recyclerView?.adapter = adapter

        val headerView: TextView? = recyclerView?.addHeaderView(R.layout.picker_item)
        headerView?.text = "我是头部"
        headerView?.setOnClickListener { toast("点击了头部") }
        val footerView: TextView? = recyclerView?.addFooterView(R.layout.picker_item)
        footerView?.text = "我是尾部"
        footerView?.setOnClickListener { toast("点击了尾部") }

        refreshLayout?.setOnRefreshLoadMoreListener(this)
    }

    override fun initData() {
        adapter?.setData(analogData())
    }

    /**
     * 模拟数据
     */
    private fun analogData(): MutableList<String?> {
        val data: MutableList<String?> = ArrayList()
        adapter?.let {
            for (i in it.getCount() until it.getCount() + 20) {
                data.add("我是第" + i + "条目")
            }
            return data
        }
        return data
    }

    /**
     * [BaseAdapter.OnItemClickListener]
     *
     * @param recyclerView      RecyclerView对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
        testInstall()
        toast(adapter?.getItem(position))
    }

    /**
     * [OnRefreshLoadMoreListener]
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        postDelayed({
            adapter?.clearData()
            adapter?.setData(analogData())
            this.refreshLayout?.finishRefresh()
        }, 1000)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        postDelayed({
            this.refreshLayout?.finishLoadMore()
            adapter?.apply {
                addData(analogData())
                setLastPage(getCount() >= 100)
                this@StatusFragment.refreshLayout?.setNoMoreData(isLastPage())
            }
        }, 1000)
    }
}