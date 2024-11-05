package com.hjq.demo.http.model

import com.hjq.http.config.IRequestApi

class VersionApi : IRequestApi {
    override fun getApi(): String {
        return "api/index/version"
    }
}