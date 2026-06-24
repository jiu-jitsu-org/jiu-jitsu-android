package com.kyu.jiu_jitsu.data.model.dto.response

import com.kyu.jiu_jitsu.data.model.UserProfileInfo
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfileResponse(
    override val success: Boolean?,
    override val code: String?,
    override val message: String?,
    override val data: UserProfileResponseData?,
) : ApiResponse<UserProfileResponseData>

@JsonClass(generateAdapter = true)
data class UserProfileResponseData (
    val userId: Int,
    val email: String?,
    val nickname: String?,
    val profileImageUrl: String?,
    val profileImage: UserProfileImageResponse?,
    val snsProvider: String?,
    val ownerRequested: Boolean?,
    val ownerRequestImage: UserProfileImageResponse?,
    val role: String?,
    val status: String?
)

@JsonClass(generateAdapter = true)
data class UserProfileImageResponse(
    val id: Int?,
    val imageUrl: String?,
)

fun UserProfileResponseData?.toInfo(): UserProfileInfo =
    UserProfileInfo(
        this?.userId,
        this?.email,
        this?.nickname,
        this?.profileImage?.imageUrl ?: this?.profileImageUrl,
        this?.snsProvider,
        this?.role,
        this?.status
    )
