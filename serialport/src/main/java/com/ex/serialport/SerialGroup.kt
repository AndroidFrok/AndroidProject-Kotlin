package com.hjq.demo.http.model

enum class SerialGroup(val portName: String = "", val minIndex: Int = 1, val maxIndex: Int = 0) {
    GroupA("/dev/ttyS2", 1, 52),
    GroupB("/dev/ttyS3", 53, 100),
    GroupC("/dev/ttyS0", 1, 52),
    GroupD("/dev/ttyS1", 1, 52),
}