package com.kyu.jiu_jitsu.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SnsLoginRequest(
    val snsProvider: String,
    val accessToken: String? = null,
    val idToken: String? = null,
)

@JsonClass(generateAdapter = true)
data class SnsLoginResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val userInfo: UserInfo?,
)