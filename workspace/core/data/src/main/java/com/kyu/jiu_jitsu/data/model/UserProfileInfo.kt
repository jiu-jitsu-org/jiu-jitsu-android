package com.kyu.jiu_jitsu.data.model

data class UserProfileInfo(
    val id: Int?,
    val email: String?,
    val nickname: String?,
    val profileImageUrl: String?,
    val snsProvider: String?,
    val role: String?,
    val status: String?
)
