package com.kyu.jiu_jitsu.data.model.dto.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AppInfoRequest(
    val fcmToken: String = "",
    val deviceId: String = "",
    val osType: String = "ANDROID",
    val osVersion: String = "",
)
