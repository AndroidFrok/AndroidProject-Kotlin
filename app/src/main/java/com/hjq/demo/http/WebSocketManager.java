package com.hjq.demo.http;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

import com.hjq.demo.manager.MmkvUtil;
import com.hjq.demo.other.AppConfig;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import timber.log.Timber;

/**
 * 本工程接口文档  https://doc.weixin.qq.com/doc/w3_AD4A9AYCAIA7ZlQL0Y1RKKc5qXpbg?scode=AH4AwAcNAAg4xVDNbFAEQA9AYCAIA
 * <p>
 * https://blog.csdn.net/fengyeNom1/article/details/115183848
 */

public class WebSocketManager extends WebSocketListener {

    private final int NORMAL_CLOSURE_STATUS = 1000;

    static public boolean isConnected() {
        return isConnected;
    }

    /**
     * 邏輯上記錄是否連接  不是100%的準確
     */
    private static boolean isConnected;
    private OkHttpClient sClient = null;
    /**
     * 如果是null  就是未连接状态
     */
    private WebSocket sWebSocket = null;
    private String mSocketURL;
    private final int ACTION_EMPTYMSG = 1400;
    private final int ACTION_FAILURE = 1401;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACTION_EMPTYMSG:
                    Map<String, String> params = new ArrayMap<>();
                    params.put("type", "3");
//                    sendData(new Gson().toJson(params));
                    // 每隔5秒发送一次心跳包，检测连接没有断开
                    handler.sendEmptyMessageDelayed(ACTION_EMPTYMSG, 5000);
//                    EventBus.getDefault().post(new AppUpdateEvent());
                    break;
                case ACTION_FAILURE:
                    handler.sendEmptyMessageDelayed(ACTION_FAILURE, 10 * 1000);
                    /*try {
                        Thread.sleep(3000);//  不能死循环执行连接  会发生OOM或空指针异常！ sleep方案会导致程序长时间未响应弹框
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                    }*/
                    if (canRetry) {
                        countDownTimer.start();
                        connectWebSocket();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    private static volatile WebSocketManager instance;

    public static WebSocketManager getInstance() {
        if (instance == null) {
            synchronized (WebSocketManager.class) {
                if (instance == null) {
                    instance = new WebSocketManager();
                }
            }
        }
        return instance;
    }

    public void connectWebSocket() {
        if (!isConnected) {
            if (sClient == null) {
                sClient = new OkHttpClient.Builder().readTimeout(45, TimeUnit.SECONDS)//设置读取超时时间
                        .writeTimeout(45, TimeUnit.SECONDS)//设置写的超时时间
                        .connectTimeout(45, TimeUnit.SECONDS)//设置连接超时时间
                        .retryOnConnectionFailure(true) // 没用。。
                        .build();
            }
            if (sWebSocket == null) {
//   todo             mSocketURL = AppConfig.INSTANCE.getSockHost();
                Request request = new Request.Builder().url(mSocketURL).build();
                sWebSocket = sClient.newWebSocket(request, this);
            }
            Timber.d("connectWebSocket");
        } else {
            Timber.d("已連接  不重複創建 ");
        }

//        reqLogin();
    }

    public boolean sendData(String info) {
        boolean sentSuccess = false;
        if (sWebSocket != null) {
            if (!TextUtils.isEmpty(info) && sWebSocket != null) {
                sentSuccess = sWebSocket.send(info);
            }
        }
        return sentSuccess;
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {

        /**
         *   ## 塔台已收到 可以降落
         */
//       Timber.d("onMessage:String " + text);
        if (!TextUtils.isEmpty(text)) {
            onGetMsg(text);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
//      Timber.d("onMessage:bytes " + bytes);
        if (!TextUtils.isEmpty(bytes.hex())) {
            onGetMsg(bytes.hex());
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
//      Timber.d("onClosing ");
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        CrashReport.postCatchedException(new Throwable("onClosing" + reason));
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        isConnected = false;
//      Timber.d("onClosed ");
        CrashReport.postCatchedException(new Throwable("onClosed" + reason));
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        isConnected = false;
        Timber.d("socket onFailure " + t.getLocalizedMessage());
        if (sWebSocket != null) {
            sWebSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye!");
            sWebSocket = null;
        }
        sClient = null;
        handler.removeMessages(ACTION_EMPTYMSG);
        handler.sendEmptyMessage(ACTION_FAILURE);
//        CrashReport.postCatchedException(new Throwable("onFailure我断开连接了"));
//        EventBus.getDefault().post(new SocketEvent(2));
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
//        CrashReport.postCatchedException(new Throwable("onOpen  开始计时~"));
//        reqLogin();
        Timber.d("onOpen ");
        isConnected = true;
        handler.removeMessages(ACTION_FAILURE);
        handler.sendEmptyMessage(ACTION_EMPTYMSG);
    }

    public void closeWebSocket() {
        if (sWebSocket != null) {
            sWebSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye!");
            sWebSocket = null;
        }
        if (sClient != null) {
            sClient = null;
        }
        if (instance != null) {
            instance = null;
        }
        handler.removeCallbacksAndMessages(null);
    }

    public void onGetMsg(String json) {
        Timber.d("塔台已收到 " + json);
//        EventBus.getDefault().post(new SocketEvent(json));
    }

    /**
     * 登录放这里面方便  其他请求放act里
     */
    public void reqLogin() {
        Map<String, String> params = new ArrayMap<>();
        params.put("type", "1");
        String id = MmkvUtil.getString(MmkvUtil.DeviceCode, "");
        params.put("sn", id);
//        sendData(new Gson().toJson(params));
    }

    /**
     * 断线重试的  倒计时方案
     * 长链接进入失败回调 3秒后开始允许重试
     */
    final int timeLong = 3 * 1000;
    private boolean canRetry = true;
    CountDownTimer countDownTimer = new CountDownTimer(timeLong, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            canRetry = false;
        }

        @Override
        public void onFinish() {
            canRetry = true;
        }
    };
}
