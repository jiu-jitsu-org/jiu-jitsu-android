package com.kyu.jiu_jitsu.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserInfo(
    val id: Int,
    val email: String?,
    val nickname: String?,
    val profileImageUrl: String?,
    val snsProvider: String?,
)