package com.hjq.demo.http.api

import com.hjq.http.config.IRequestApi

open class BaseEasyHttp : IRequestApi {

//    private var device_code = MmkvUtil.getString(MmkvUtil.MN, "-1")
    var page_size = 10
    var page = 1

    /*   fun setCurrengPage(page: Int?): IRequestApi = apply {
           this.page = page!!
       }*/

    override fun getApi(): String {
        return ""
    }
}