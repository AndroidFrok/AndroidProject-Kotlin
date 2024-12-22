package com.hjq.demo.http.api

import com.hjq.http.config.IRequestApi

class VersionApi : IRequestApi {
    override fun getApi(): String {
        return "/api/machine/index/version"
    }
}