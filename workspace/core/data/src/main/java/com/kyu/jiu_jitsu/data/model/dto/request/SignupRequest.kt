package com.kyu.jiu_jitsu.data.model.dto.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignupRequest(
    val nickname: String,
    val isMarketingAgreed: Boolean,
)
