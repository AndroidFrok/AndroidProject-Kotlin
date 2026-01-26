package com.hjq.demo.http

import android.os.Handler
import android.os.Message
import android.util.Log
import com.hjq.demo.services.SocketEvent
import org.greenrobot.eventbus.EventBus
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.net.UnknownHostException

object TcpClientJava {
    var isConnected = false
        private set
    private var socket: Socket? = null
    private var writer: BufferedWriter? = null
    private var reader: BufferedReader? = null
    private var line: String? = null
    private const val TAG = "tcpclient"

    fun connect() {
        if (!isConnected) {
            Thread {
                val IPAdr = "39.98.108.188"
                val PORT = 10210
                try {
                    socket = Socket(IPAdr, PORT)
                    writer = BufferedWriter(OutputStreamWriter(socket!!.getOutputStream()))
                    reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                    val buf = CharArray(2048)
                    var i: Int
                    while (reader!!.read(buf, 0, 100).also { i = it } != -1) {
                        line = String(buf, 0, i)
                        val msg = handler.obtainMessage()
                        msg.obj = line
                        handler.sendMessage(msg)
                    }
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                    isConnected = false
                } catch (e: IOException) {
                    e.printStackTrace()
                    isConnected = false
                }
            }.start()
            isConnected = true
            EventBus.getDefault().post(SocketEvent("", 0))
        } else {
            isConnected = false
        }
    }

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            EventBus.getDefault().post(SocketEvent(line, 1))
            Log.i("PDA", "---TCP--->$line")
        }
    }

    fun send(s: String) {
        if (writer == null) return
        Thread {
            try {
                writer?.write("$s\n")
                writer?.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }
}
