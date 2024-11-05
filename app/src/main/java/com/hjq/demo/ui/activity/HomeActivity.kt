package com.hjq.demo.ui.activity

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.gyf.immersionbar.ImmersionBar
import com.hjq.base.BaseDialog
import com.hjq.base.FragmentPagerAdapter
import com.hjq.demo.R
import com.hjq.demo.app.AppActivity
import com.hjq.demo.app.AppFragment
import com.hjq.demo.http.api.MyIntercept
import com.hjq.demo.http.model.VersionApi
import com.hjq.demo.http.model.VersionM
import com.hjq.demo.manager.*
import com.hjq.demo.other.AppConfig
import com.hjq.demo.other.DoubleClickHelper
import com.hjq.demo.ui.adapter.NavigationAdapter
import com.hjq.demo.ui.dialog.UpdateDialog
import com.hjq.demo.ui.fragment.FindFragment
import com.hjq.demo.ui.fragment.HomeFragment
import com.hjq.demo.ui.fragment.MessageFragment
import com.hjq.demo.ui.fragment.MineFragment
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallback
import com.hjq.toast.ToastUtils
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener
import timber.log.Timber

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/10/18
 *    desc   : 首页界面
 */
@Deprecated("")
class HomeActivity : AppActivity(), NavigationAdapter.OnNavigationListener {

    companion object {

        private const val INTENT_KEY_IN_FRAGMENT_INDEX: String = "fragmentIndex"
        private const val INTENT_KEY_IN_FRAGMENT_CLASS: String = "fragmentClass"

        @JvmOverloads
        fun start(
            context: Context,
            fragmentClass: Class<out AppFragment<*>?>? = HomeFragment::class.java
        ) {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra(INTENT_KEY_IN_FRAGMENT_CLASS, fragmentClass)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private val viewPager: ViewPager? by lazy { findViewById(R.id.vp_home_pager) }
    private val navigationView: RecyclerView? by lazy { findViewById(R.id.rv_home_navigation) }
    private var navigationAdapter: NavigationAdapter? = null
    private var pagerAdapter: FragmentPagerAdapter<AppFragment<*>>? = null

    override fun getLayoutId(): Int {
        return R.layout.home_activity
    }

    override fun initView() {
        navigationAdapter = NavigationAdapter(this).apply {
            addItem(
                NavigationAdapter.MenuItem(
                    getString(R.string.home_nav_index),
                    ContextCompat.getDrawable(this@HomeActivity, R.drawable.home_home_selector)
                )
            )
            addItem(
                NavigationAdapter.MenuItem(
                    getString(R.string.home_nav_found),
                    ContextCompat.getDrawable(this@HomeActivity, R.drawable.home_found_selector)
                )
            )
            addItem(
                NavigationAdapter.MenuItem(
                    getString(R.string.home_nav_message),
                    ContextCompat.getDrawable(this@HomeActivity, R.drawable.home_message_selector)
                )
            )
            addItem(
                NavigationAdapter.MenuItem(
                    getString(R.string.home_nav_me),
                    ContextCompat.getDrawable(this@HomeActivity, R.drawable.home_me_selector)
                )
            )
            setOnNavigationListener(this@HomeActivity)
            navigationView?.adapter = this
        }
    }

    override fun initData() {
        pagerAdapter = FragmentPagerAdapter<AppFragment<*>>(this).apply {
            addFragment(HomeFragment.newInstance())
            addFragment(FindFragment.newInstance())
            addFragment(MessageFragment.newInstance())
            addFragment(MineFragment.newInstance())
            viewPager?.adapter = this
        }
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        pagerAdapter?.let {
            switchFragment(it.getFragmentIndex(getSerializable(INTENT_KEY_IN_FRAGMENT_CLASS)))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewPager?.let {
            // 保存当前 Fragment 索引位置
            outState.putInt(INTENT_KEY_IN_FRAGMENT_INDEX, it.currentItem)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // 恢复当前 Fragment 索引位置
        switchFragment(savedInstanceState.getInt(INTENT_KEY_IN_FRAGMENT_INDEX))
    }

    private fun switchFragment(fragmentIndex: Int) {
        if (fragmentIndex == -1) {
            return
        }
        when (fragmentIndex) {
            0, 1, 2, 3 -> {
                viewPager?.currentItem = fragmentIndex
                navigationAdapter?.setSelectedPosition(fragmentIndex)
            }
        }
    }

    /**
     * [NavigationAdapter.OnNavigationListener]
     */
    override fun onNavigationItemSelected(position: Int): Boolean {
        return when (position) {
            0, 1, 2, 3 -> {
                viewPager?.currentItem = position
                true
            }

            else -> false
        }
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig() // 指定导航栏背景颜色
            .navigationBarColor(R.color.white)
    }

    override fun onBackPressed() {
        if (!DoubleClickHelper.isOnDoubleClick()) {
            toast(R.string.home_exit_hint)
            MessageDialog.build().setTitle("dasdasd").setOkButton("OKKKKKKK")
                .show()
                .setCancelButton("can").okButtonClickListener =
                object : OnDialogButtonClickListener<MessageDialog> {
                    override fun onClick(dialog: MessageDialog?, v: View?): Boolean {

                        return false
                    }

                }

            return
        }

        // 移动到上一个任务栈，避免侧滑引起的不良反应
        moveTaskToBack(false)
        postDelayed({
            // 进行内存优化，销毁掉所有的界面
            ActivityManager.getInstance().finishAllActivities()
        }, 300)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager?.adapter = null
        navigationView?.adapter = null
        navigationAdapter?.setOnNavigationListener(null)
    }


    /**
     *   李山河  请求范例   每个请求加上延时逻辑
     */
    private fun reqVersion() {
        val api = VersionApi()
        EasyHttp.get(this).api(api).delay(MyIntercept.getRandomDelay())
            .request(object : HttpCallback<VersionM>(this) {
                override fun onSucceed(result: VersionM) {
                    super.onSucceed(result)
                    if (result.getCode() === 1) {
                        val currentV = AppConfig.getVersionCode()
                        var serverV: String = result.getData().getNewversion()
                        if (serverV == "") {
                            serverV = "0"
                        }
                        val v = serverV.toInt()
                        Timber.d("当前版本 $currentV,$v")
                        if (currentV < v) {
                            doDownload(result.getData().getDownloadurl())
                        }
                    } else {
                        ToastUtils.show("" + result.getMsg())
                    }
                }
            })
    }

    var isUpdateDialogShow = false

    /**
     * 进行下载 安装新版本
     */
    private fun doDownload(url: String) {
        if (isUpdateDialogShow) {
            return
        }
        val dialog = UpdateDialog.Builder(this)
        dialog.setDownloadUrl(url).setUpdateLog(getString(R.string.waiting4upgrade))
            .addOnShowListener(object : BaseDialog.OnShowListener {
                override fun onShow(dialog: BaseDialog?) {
                    true.also { isUpdateDialogShow = it }
                }
            }).addOnCancelListener(object : BaseDialog.OnCancelListener {
                override fun onCancel(dialog: BaseDialog?) {
                    true.also { isUpdateDialogShow = it }
                }

            }).addOnDismissListener(object : BaseDialog.OnDismissListener {
                override fun onDismiss(dialog: BaseDialog?) {
                    true.also { isUpdateDialogShow = it }
                }

            })

        dialog.show()
    }
}