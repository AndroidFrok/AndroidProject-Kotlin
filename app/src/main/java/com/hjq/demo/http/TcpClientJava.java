package com.hjq.demo.http;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hjq.demo.services.SocketEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClientJava {
    static public  boolean isConnected = false;
    static Socket socket = null;
    static BufferedWriter writer = null;
    static BufferedReader reader = null;
    static private String line;
    static private String TAG = "tcpclient";

    static public void connect() {
        if (false == isConnected) {
            new Thread() {
                public void run() {
                    String IPAdr = "39.98.108.188";
                    int PORT = 10210;
                    try {
                        /* 建立socket */
                        socket = new Socket(IPAdr, PORT);
                        /* 输出流 */
                        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        /* 输入流 */
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        /* 调试输出 */
//                        Log.i(TAG, "输入输出流获取成功");
//                        Log.i(TAG, "检测数据");
                        /* 读数据并更新UI */
                        char[] buf = new char[2048];
                        int i;
                        while ((i = reader.read(buf, 0, 100)) != -1) {
                            line = new String(buf, 0, i);
                            Message msg = handler.obtainMessage();
                            msg.obj = line;
                            handler.sendMessage(msg);
//                            Log.i(TAG, "send to handler");
                        }
                    } catch (UnknownHostException e) {
//                        Toast.makeText(ClientActivity.this,"无法建立连接：）",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        isConnected = false;
                    } catch (IOException e) {

                        e.printStackTrace();
                        isConnected = false;
                    }
                }
            }.start();
            isConnected = true;
            EventBus.getDefault().post(new SocketEvent("", 0));
            /* 更新UI */
//            btn_connect.setText("断开");
//            Toast.makeText(ClientActivity.this,"连接成功：）",Toast.LENGTH_SHORT).show();
        } else {
            isConnected = false;
            /* 更新UI */

        }
    }


    static private Handler handler = new Handler() {
        @Override
        /* 当有消息发送出来的时候就执行Handler的这个方法 */

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            /* 更新UI */
            EventBus.getDefault().post(new SocketEvent(line, 1));
            /* 调试输出 */
            Log.i("PDA", "---TCP-->" + line);
        }
    };


    /* 发送按钮处理函数：向输出流写数据 */
    static public void send(String s) {
        if (writer == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /* 向输出流写数据 */
                    writer.write(s + "\n");
                    writer.flush();
                    /* 更新UI */
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
}
