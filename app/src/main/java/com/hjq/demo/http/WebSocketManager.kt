package com.hjq.demo.http

import com.hjq.demo.manager.MmkvUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

/**
 * 本工程接口文档  https://doc.weixin.qq.com/doc/w3_AD4A9AYCAIA7ZlQL0Y1RKKc5qXpbg?scode=AH4AwAcNAAg4xVDNbFAEQA9AYCAIA
 *
 * https://blog.csdn.net/fengyeNom1/article/details/115183848
 */
class WebSocketManager private constructor() : WebSocketListener(), CoroutineScope {

    private val NORMAL_CLOSURE_STATUS = 1000

    var socketURL: String? = null
        set

    val isConnected: Boolean
        get() = _isConnected

    private var _isConnected = false
    private var sClient: OkHttpClient? = null
    private var sWebSocket: WebSocket? = null

    // 协程作用域
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = kotlinx.coroutines.Dispatchers.Main + job

    private var heartbeatJob: Job? = null
    private var retryJob: Job? = null
    private var canRetry = true

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
                val url = socketURL ?: return
                val request = Request.Builder().url(url).build()
                sWebSocket = sClient!!.newWebSocket(request, this)
            }
            Timber.d("connectWebSocket")
        } else {
            Timber.d("已連接  不重複創建 ")
        }
    }

    fun sendData(info: String?): Boolean {
        if (sWebSocket != null && !info.isNullOrEmpty()) {
            return sWebSocket!!.send(info)
        }
        return false
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        if (text.isNotEmpty()) {
            onGetMsg(text)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        if (bytes.hex().isNotEmpty()) {
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

        // 取消心跳
        heartbeatJob?.cancel()
        heartbeatJob = null

        // 启动重试逻辑
        scheduleRetry()
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Timber.d("onOpen ")
        _isConnected = true

        // 取消重试
        retryJob?.cancel()
        retryJob = null

        // 启动心跳
        startHeartbeat()
    }

    private fun startHeartbeat() {
        heartbeatJob = launch {
            while (_isConnected) {
                delay(5000)
                if (_isConnected) {
                    // 心跳包 - 可根据需要发送数据
                    // val params = mapOf("type" to "3")
                    // sendData(Gson().toJson(params))
                }
            }
        }
    }

    private fun scheduleRetry() {
        if (retryJob?.isActive == true) return

        // 先禁用重试3秒
        canRetry = false
        launch {
            delay(3000)
            canRetry = true
        }

        retryJob = launch {
            delay(10 * 1000)
            if (canRetry) {
                connectWebSocket()
            }
        }
    }

    fun closeWebSocket() {
        heartbeatJob?.cancel()
        retryJob?.cancel()
        sWebSocket?.close(NORMAL_CLOSURE_STATUS, "Goodbye!")
        sWebSocket = null
        sClient = null
    }

    fun onGetMsg(json: String) {
        Timber.d("塔台已收到 $json")
    }

    fun reqLogin() {
        val params = mutableMapOf<String, String>()
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
