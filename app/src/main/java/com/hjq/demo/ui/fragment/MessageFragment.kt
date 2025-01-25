package com.hjq.demo.ui.fragment

import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.alibaba.android.arouter.launcher.ARouter
import com.hjq.demo.R
import com.hjq.demo.aop.Permissions
import com.hjq.demo.app.TitleBarFragment
import com.hjq.demo.manager.Router
import com.hjq.demo.ui.activity.AdminActivity
import com.hjq.demo.ui.activity.HomeActivity
import com.hjq.permissions.Permission
import timber.log.Timber

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/10/18
 *    desc   : 消息 Fragment
 */
class MessageFragment : TitleBarFragment<HomeActivity>() {

    companion object {

        fun newInstance(): MessageFragment {
            return MessageFragment()
        }
    }

    private var imageView: ImageView? = null

    override fun getLayoutId(): Int {
        return R.layout.message_fragment
    }

    override fun initView() {
        imageView = activity?.findViewById(com.hjq.demo.R.id.iv_message_image)/*setOnClickListener(
            R.id.btn_message_image1,
            R.id.btn_message_image2,
            R.id.btn_message_image3,
            R.id.btn_message_toast,
            R.id.btn_message_permission,
            R.id.btn_message_setting,
            R.id.btn_message_black,
            R.id.btn_message_white,
            R.id.btn_message_tab
        )*/
        if (getAttachActivity() == null) {
            Timber.e("aaaa");
            return;
        }
        var a: AppCompatButton? = getAttachActivity()?.findViewById(R.id.btn_message_image3)
        var btn_message_image1: AppCompatButton? =
            getAttachActivity()?.findViewById(R.id.btn_message_image1)
        a?.setOnClickListener {
            toast("aaaaaaa");
        }
        btn_message_image1?.setOnClickListener {

            ARouter.getInstance().build(Router.Main).navigation();
        }
        btn_message_image1?.setOnLongClickListener {
            startActivity(AdminActivity::class.java)
            true
        }

    }

    override fun initData() {}


    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    /*@SingleClick
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_message_image1 -> {
                imageView?.let {
                    it.visibility = View.VISIBLE
                    GlideApp.with(this).load("https://www.baidu.com/img/bd_logo.png").into(it)
                }
            }

            R.id.btn_message_image2 -> {
                imageView?.let {
                    it.visibility = View.VISIBLE
                    GlideApp.with(this).load("https://www.baidu.com/img/bd_logo.png").circleCrop()
                        .into(it)
                }
            }

            R.id.btn_message_image3 -> {

                imageView?.let {
                    it.visibility = View.VISIBLE
                    GlideApp.with(this).load("https://www.baidu.com/img/bd_logo.png")
                        .transform(RoundedCorners(resources.getDimension(R.dimen.dp_20).toInt()))
                        .into(it)
                }
            }

            R.id.btn_message_toast -> {

                toast("我是吐司")

            }

            R.id.btn_message_permission -> {

                requestPermission()
            }

            R.id.btn_message_setting -> {

                XXPermissions.startPermissionActivity(this)
            }

            R.id.btn_message_black -> {
            }

            R.id.btn_message_white -> {


            }

            R.id.btn_message_tab -> {

                HomeActivity.start(getAttachActivity()!!, HomeFragment::class.java)
            }
        }
    }*/

    @Permissions(Permission.CAMERA)
    private fun requestPermission() {
        toast("获取摄像头权限成功")
    }
}