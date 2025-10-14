package com.kyu.jiu_jitsu.data.model

data class LoginInfo(
    val accessToken: String,
    val refreshToken: String,
    val userInfo: UserInfo? = null,
)

data class UserInfo(
    val id: Int,
    val email: String?,
    val nickname: String?,
    val profileImageUrl: String?,
    val snsProvider: String?,
)