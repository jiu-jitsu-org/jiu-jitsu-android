package kr.bjj_oss.data.model.dto.response

import kr.bjj_oss.model.UserProfileInfo
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

/** Maps the backend profile shape to the smaller app-facing account model. */
internal fun UserProfileResponseData.toInfo(): UserProfileInfo =
    UserProfileInfo(
        id = userId,
        email = email,
        nickname = nickname,
        profileImageUrl = profileImage?.imageUrl ?: profileImageUrl,
        snsProvider = snsProvider,
        role = role,
        status = status,
    )
