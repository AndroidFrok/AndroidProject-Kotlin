package com.hjq.demo.http

import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.ArrayMap
import androidx.annotation.NonNull
import com.hjq.demo.manager.MmkvUtil
import com.hjq.demo.other.AppConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * 本工程接口文档  https://doc.weixin.qq.com/doc/w3_AD4A9AYCAIA7ZlQL0Y1RKKc5qXpbg?scode=AH4AwAcNAAg4xVDNbFAEQA9AYCAIA
 *
 * https://blog.csdn.net/fengyeNom1/article/details/115183848
 */
class WebSocketManager private constructor() : WebSocketListener() {

    private val NORMAL_CLOSURE_STATUS = 1000

    var socketURL: String? = null
        set

    val isConnected: Boolean
        get() = _isConnected

    private var _isConnected = false
    private var sClient: OkHttpClient? = null
    private var sWebSocket: WebSocket? = null
    private val ACTION_EMPTYMSG = 1400
    private val ACTION_FAILURE = 1401

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(@NonNull msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                ACTION_EMPTYMSG -> {
                    val params = ArrayMap<String, String>()
                    params["type"] = "3"
                    handler.sendEmptyMessageDelayed(ACTION_EMPTYMSG, 5000)
                }
                ACTION_FAILURE -> {
                    handler.sendEmptyMessageDelayed(ACTION_FAILURE, 10 * 1000)
                    if (canRetry) {
                        countDownTimer?.start()
                        connectWebSocket()
                    }
                }
                else -> {}
            }
        }
    }

    private var countDownTimer: CountDownTimer? = null
    private var canRetry = true

    init {
        val timeLong = 3 * 1000
        countDownTimer = object : CountDownTimer(timeLong.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                canRetry = false
            }

            override fun onFinish() {
                canRetry = true
            }
        }
    }

    fun connectWebSocket() {
        if (!_isConnected) {
            if (sClient == null) {
                sClient = OkHttpClient.Builder()
                    .readTimeout(45, TimeUnit.SECONDS)
                    .writeTimeout(45, TimeUnit.SECONDS)
                    .connectTimeout(45, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build()
            }
            if (sWebSocket == null) {
                val url = socketURL ?: AppConfig.sockHost
                val request = Request.Builder().url(url).build()
                sWebSocket = sClient!!.newWebSocket(request, this)
            }
            Timber.d("connectWebSocket")
        } else {
            Timber.d("已連接  不重複創建 ")
        }
    }

    fun sendData(info: String?): Boolean {
        if (sWebSocket != null && !TextUtils.isEmpty(info)) {
            return sWebSocket!!.send(info)
        }
        return false
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        if (!TextUtils.isEmpty(text)) {
            onGetMsg(text)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        if (!TextUtils.isEmpty(bytes.hex())) {
            onGetMsg(bytes.hex())
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        _isConnected = false
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        _isConnected = false
        Timber.d("socket onFailure ${t.localizedMessage}")
        sWebSocket?.close(NORMAL_CLOSURE_STATUS, "Goodbye!")
        sWebSocket = null
        sClient = null
        handler.removeMessages(ACTION_EMPTYMSG)
        handler.sendEmptyMessage(ACTION_FAILURE)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Timber.d("onOpen ")
        _isConnected = true
        handler.removeMessages(ACTION_FAILURE)
        handler.sendEmptyMessage(ACTION_EMPTYMSG)
    }

    fun closeWebSocket() {
        sWebSocket?.close(NORMAL_CLOSURE_STATUS, "Goodbye!")
        sWebSocket = null
        sClient = null
        handler.removeCallbacksAndMessages(null)
    }

    fun onGetMsg(json: String) {
        Timber.d("塔台已收到 $json")
    }

    fun reqLogin() {
        val params = ArrayMap<String, String>()
        params["type"] = "1"
        val id = MmkvUtil.getString(MmkvUtil.MN, "")
        params["sn"] = id ?: ""
    }

    companion object {
        @Volatile
        private var instance: WebSocketManager? = null

        @JvmStatic
        fun getInstance(): WebSocketManager = synchronized(this) {
            instance ?: WebSocketManager().also { instance = it }
        }
    }
}
