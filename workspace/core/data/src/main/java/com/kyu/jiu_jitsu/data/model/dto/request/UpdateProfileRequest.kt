package com.kyu.jiu_jitsu.data.model.dto.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateProfileRequest(
    val nickname: String,
    val profileImageUrl: String,
)
