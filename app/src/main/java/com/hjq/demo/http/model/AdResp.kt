package com.hjq.demo.http.model

import com.google.gson.annotations.SerializedName

data class AdResp(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("msg")
    val msg: String? = null,
    @SerializedName("time")
    val time: String? = null,
    @SerializedName("data")
    val data: DataDTO? = null
) {
    data class DataDTO(
        @SerializedName("ad")
        val ad: AdDTO? = null,
        @SerializedName("mp3")
        val mp3: String? = null
    ) {
        data class AdDTO(
            @SerializedName("id")
            val id: Int? = null,
            @SerializedName("image")
            val image: String? = null,
            @SerializedName("start_time_text")
            val startTimeText: String? = null,
            @SerializedName("end_time_text")
            val endTimeText: String? = null,
            @SerializedName("type_text")
            val typeText: String? = null
        )
    }
}
