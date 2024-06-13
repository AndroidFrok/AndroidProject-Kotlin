package com.hjq.demo.wxapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;

import com.hjq.demo.http.glide.ImageUtils;
import com.hjq.toast.ToastUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WeChatOpenPlatform {
    static public String OpenWeiXinAppid = "";

    private static void init(Context context) {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(context, OpenWeiXinAppid, false);

        // 将应用的appId注册到微信
//        api.registerApp(OpenWeiXinAppid);

        //建议动态监听微信启动广播进行注册到微信
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // 将该app注册到微信
                api.registerApp(OpenWeiXinAppid);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
    }

    // IWXAPI 是第三方app和微信通信的openApi接口
    private static IWXAPI api;

    public static void regToWx(Context context) {
        init(context);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "hello  hacker ,you son of bitch";
        api.sendReq(req);
    }

    public static void shareWebsite(Context context, int scene, String webUrl, String title, String content, Bitmap bitmap) {
        if (bitmap == null) {
            ToastUtils.show("数据异常");
            return;
        }
        init(context);
        if (!api.isWXAppInstalled()) {
            ToastUtils.show("没有安装微信");
            return;
        }

        // 初始化一个WXWebpageObject对象
        WXWebpageObject webpageObject = new WXWebpageObject();
        // 填写网页的url
        webpageObject.webpageUrl = webUrl;

        // 用WXWebpageObject对象初始化一个WXMediaMessage对象

        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        // 填写网页标题、描述、位图
        msg.title = title;
        msg.description = content;
        // 如果没有位图，可以传null，会显示默认的图片
//        if (bitmap != null)
        msg.setThumbImage(ImageUtils.imageZoom(bitmap));

//        msg.thumbData = ImageUtils.bmpToByteArray(bitmap, true, 31);
        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        // transaction用于唯一标识一个请求（可自定义）
        req.transaction = "webpage";
        // 上文的WXMediaMessage对象
        req.message = msg;
        //1 SendMessageToWX.Req.WXSceneSession是分享到好友会话
        //2 SendMessageToWX.Req.WXSceneTimeline是分享到朋友圈
        if (scene == 1) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        // 向微信发送请求
        api.sendReq(req);
    }

    /**
     * 微信  -分享图片
     *
     * @param context
     * @param scene   1 微信聊天  ；2 朋友圈
     * @param title
     * @param content
     * @param bmp     使用案例
     *                String url = "http://zhukao.appppa.cn/assets/images/logo.png";
     *                XXPermissions.with(this).permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE).request(new OnPermissionCallback() {
     * @Override public void onGranted(List<String> permissions, boolean all) {
     * FileUtil.donwloadImg(getActivity(), url, file -> {
     * Bitmap bitmap = null;
     * if (file == null) {
     * bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.launcher_ic);
     * } else {
     * DebugLogUtil.getInstance().Debug(file.getAbsolutePath());
     * bitmap = BitmapFactory.decodeFile(file.getPath());
     * }
     * WeChatOpenPlatform.sharePic(getContext(), 1, "测试标题", "内容", bitmap);
     * });
     * }
     * });
     */
    public static void sharePic(Context context, int scene, String title, String content, Bitmap bmp) {
//初始化 WXImageObject 和 WXMediaMessage 对象
        init(context);
        WXImageObject imgObj = new WXImageObject(bmp);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

//设置缩略图
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 50, 50, true);
        bmp.recycle();
        msg.thumbData = ImageUtils.bmpToByteArray(thumbBmp, true);

//构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        if (scene == 1) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
//        req.userOpenId = getOpenId();
//调用api接口，发送数据到微信
        api.sendReq(req);

    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    static public void launchMini(Context context) {
        // 填移动应用(App)的 AppId，非小程序的 AppID
        IWXAPI api = WXAPIFactory.createWXAPI(context, OpenWeiXinAppid);

        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = "gh_20904ab5bc68"; // 填小程序原始id
//        req.path = "";                  ////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        api.sendReq(req);
    }
}
