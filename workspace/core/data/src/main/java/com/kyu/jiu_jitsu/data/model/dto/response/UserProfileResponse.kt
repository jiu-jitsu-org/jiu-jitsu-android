package com.kyu.jiu_jitsu.data.model.dto.response

import com.kyu.jiu_jitsu.data.model.UserProfileInfo
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfileResponse(
    val success: Boolean?,
    val code: String?,
    val message: String?,
    val data: UserProfileResponseData?,
)

@JsonClass(generateAdapter = true)
data class UserProfileResponseData (
    val userId: Int,
    val email: String?,
    val nickname: String?,
    val profileImageUrl: String?,
    val snsProvider: String?,
    val role: String?,
    val status: String?
)

fun UserProfileResponseData?.toInfo(): UserProfileInfo =
    UserProfileInfo(
        this?.userId,
        this?.email,
        this?.nickname,
        this?.profileImageUrl,
        this?.snsProvider,
        this?.role,
        this?.status
    )