package com.hjq.demo.wxapi

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/05/06
 * desc   : 微信登录回调（请注意这个 Activity 放置的包名要和当前项目的包名保持一致，否则将不能正常回调）

class WXEntryActivity : Activity(), IWXAPIEventHandler {

    private var api: IWXAPI? = null
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = WXAPIFactory.createWXAPI(this, WeChatOpenPlatform.OpenWeiXinAppid, false)
        api?.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        api?.handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq?) {
        when (req?.type) {
            ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX -> {}
            ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX -> {}
            ConstantsAPI.COMMAND_LAUNCH_BY_WX -> {}
            else -> {}
        }
    }

    override fun onResp(baseResp: BaseResp?) {
    }
}
*/
