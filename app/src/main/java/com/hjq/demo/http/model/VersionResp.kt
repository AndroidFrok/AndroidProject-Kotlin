package com.hjq.demo.http.model

//data class VersionRespp(val code: Int, val msg: String, val data1: abbbc, val data: Version) :BaseModel() {
data class VersionResp(val data1: abbbc, val data: Version) : BaseModel() {

    data class Version(
        val version_code: String, val versionName: String, val download_url: String
    )

    data class abbbc(
        val version_code: String, val versionName: String, val download_url: String
    )
}