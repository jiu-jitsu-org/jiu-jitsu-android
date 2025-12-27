package com.kyu.jiu_jitsu.data.model.dto.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckNicknameResponse(
    val success: Boolean?,
    val code: String?,
    val message: String?,
    val data: Boolean?,
)
