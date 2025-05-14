package io.dcloud.uniplugin;

import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;


public class TestModule extends UniModule {

    String TAG = "TestModule";

    //run ui thread
    @UniJSMethod(uiThread = true)
    public void testAsyncFunc(JSONObject options, UniJSCallback callback) {
        Log.e(TAG, "testAsyncFunc worked --"+ options);
        if(callback != null) {
            JSONObject data = new JSONObject();
            data.put("code", "success");
//            callback.invoke(data);
            callback.invokeAndKeepAlive(data);
        }
    }

    //run JS thread
    @UniJSMethod (uiThread = false)
    public JSONObject testSyncFunc(){
        JSONObject data = new JSONObject();
        data.put("code", "success");
        return data;
    }

    @UniJSMethod (uiThread = true)
    public void gotoNativePage(){
        if(mUniSDKInstance != null) {
            Intent intent = new Intent(mUniSDKInstance.getContext(), NativePageActivity.class);
            mUniSDKInstance.getContext().startActivity(intent);
        }
    }

    @UniJSMethod (uiThread = true)
    public void gotoHostNativePage() {
        if(mUniSDKInstance != null) {
            Intent intent = new Intent();
            intent.setClassName(mUniSDKInstance.getContext(), "com.example.unimpdemo.DialogActivity");
            mUniSDKInstance.getContext().startActivity(intent);
        }
    }
}
