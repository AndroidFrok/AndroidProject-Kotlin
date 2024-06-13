package com.hjq.demo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import timber.log.Timber;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/05/06
 * desc   : 微信登录回调（请注意这个 Activity 放置的包名要和当前项目的包名保持一致，否则将不能正常回调）
 */
public final class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    private String token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        finish();// 本页面必须在任何一个可能打开的地方进行关闭操作 否则会有透明的act展示给用户
//        setContentView(R.layout.pay_result);
        api = WXAPIFactory.createWXAPI(this, WeChatOpenPlatform.OpenWeiXinAppid, false);
        api.handleIntent(getIntent(), this);
//        initClick();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
//        Toast.makeText(this, "openid = " + req.openId, Toast.LENGTH_SHORT).show();

        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//                goToGetMsg();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//                goToShowMsg((ShowMessageFromWX.Req) req);
                break;
            case ConstantsAPI.COMMAND_LAUNCH_BY_WX:
//			Toast.makeText(this, R.string.launch_from_wx, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp baseResp) {

    }

   /*@Override
    public void onResp(BaseResp resp) {
        super.onResp(resp);
        if (resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) resp;
            String extraData = launchMiniProResp.extMsg; //对应小程序组件 <button open-type="launchApp"> 中的 app-parameter 属性
            Timber.d("extraData" + extraData);
        }
    }*/
}