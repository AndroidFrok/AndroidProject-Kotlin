/*
package com.hjq.demo.wxapi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap

import com.hjq.demo.http.glide.ImageUtils
import com.hjq.toast.ToastUtils
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

object WeChatOpenPlatform {
    var OpenWeiXinAppid: String = ""

    private fun init(context: Context) {
        api = WXAPIFactory.createWXAPI(context, OpenWeiXinAppid, false)
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                api.registerApp(OpenWeiXinAppid)
            }
        }, IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP))
    }

    private var api: IWXAPI? = null

    fun regToWx(context: Context) {
        init(context)
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.state = "hello  hacker ,you son of bitch"
        api?.sendReq(req)
    }

    fun shareWebsite(context: Context, scene: Int, webUrl: String, title: String, content: String, bitmap: Bitmap?) {
        if (bitmap == null) {
            ToastUtils.show("数据异常")
            return
        }
        init(context)
        if (api?.isWXAppInstalled != true) {
            ToastUtils.show("没有安装微信")
            return
        }

        val webpageObject = WXWebpageObject().apply {
            webpageUrl = webUrl
        }

        val msg = WXMediaMessage(webpageObject).apply {
            this.title = title
            description = content
            setThumbImage(ImageUtils.imageZoom(bitmap))
        }

        val req = SendMessageToWX.Req().apply {
            transaction = "webpage"
            message = msg
            this.scene = if (scene == 1) {
                SendMessageToWX.Req.WXSceneSession
            } else {
                SendMessageToWX.Req.WXSceneTimeline
            }
        }
        api?.sendReq(req)
    }

    fun sharePic(context: Context, scene: Int, title: String, content: String, bmp: Bitmap) {
        init(context)
        val imgObj = WXImageObject(bmp)
        val msg = WXMediaMessage().apply {
            mediaObject = imgObj
            val thumbBmp = Bitmap.createScaledBitmap(bmp, 50, 50, true)
            bmp.recycle()
            thumbData = ImageUtils.bmpToByteArray(thumbBmp, true)
        }

        val req = SendMessageToWX.Req().apply {
            transaction = buildTransaction("img")
            message = msg
            this.scene = if (scene == 1) {
                SendMessageToWX.Req.WXSceneSession
            } else {
                SendMessageToWX.Req.WXSceneTimeline
            }
        }
        api?.sendReq(req)
    }

    private fun buildTransaction(type: String?): String {
        return if (type == null) System.currentTimeMillis().toString() else type + System.currentTimeMillis()
    }

    fun launchMini(context: Context) {
        val api = WXAPIFactory.createWXAPI(context, OpenWeiXinAppid)
        val req = WXLaunchMiniProgram.Req().apply {
            userName = "gh_20904ab5bc68"
            miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
        }
        api.sendReq(req)
    }
}
*/
