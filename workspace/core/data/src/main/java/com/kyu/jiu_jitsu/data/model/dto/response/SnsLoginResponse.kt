package com.kyu.jiu_jitsu.data.model.dto.response

import com.kyu.jiu_jitsu.data.model.LoginInfo
import com.kyu.jiu_jitsu.data.model.UserInfo
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SnsLoginResponse(
    val success: Boolean?,
    val code: String?,
    val message: String?,
    val data: SnsLoginResponseData?,
)

@JsonClass(generateAdapter = true)
data class SnsLoginResponseData(
    val accessToken: String?,
    val refreshToken: String?,
    val userInfo: UserInfoResponseData?,
)

@JsonClass(generateAdapter = true)
data class UserInfoResponseData(
    val id: Int,
    val email: String?,
    val nickname: String?,
    val profileImageUrl: String?,
    val snsProvider: String?,
)

fun SnsLoginResponseData?.toInfo(): LoginInfo =
    LoginInfo(
        this?.accessToken ?: "",
        this?.refreshToken ?: "",
        UserInfo(
            this?.userInfo?.id ?: 0,
            this?.userInfo?.email,
            this?.userInfo?.nickname,
            this?.userInfo?.profileImageUrl,
            this?.userInfo?.snsProvider,
        )
    )