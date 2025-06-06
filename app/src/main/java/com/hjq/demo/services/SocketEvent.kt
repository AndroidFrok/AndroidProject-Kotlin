package com.hjq.demo.services

class SocketEvent {
    var json:String = "";

    /**
     *  0-心跳
     *  1-处理数据：
     */
    var socketType  = 0;

    constructor(json: String, socketType: Int) {
        this.json = json
        this.socketType = socketType
    }


}