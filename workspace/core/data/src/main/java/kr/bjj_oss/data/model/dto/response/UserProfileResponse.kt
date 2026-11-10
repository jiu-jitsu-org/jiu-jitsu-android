package kr.bjj_oss.data.model.dto.response

import com.squareup.moshi.JsonClass
import kr.bjj_oss.model.*

@JsonClass(generateAdapter = true)
data class UserProfileResponse(
    override val success: Boolean?,
    override val code: String?,
    override val message: String?,
    override val data: UserProfileResponseData?,
) : ApiResponse<UserProfileResponseData>

@JsonClass(generateAdapter = true)
data class UserProfileResponseData(
    val userId: Int? = null,
    val email: String? = null,
    val nickname: String? = null,
    val profileImageUrl: String? = null,
    val profileImage: UserProfileImageResponse? = null,
    val snsProvider: String? = null,
    val ownerRequested: Boolean? = null,
    val ownerRequestImage: UserProfileImageResponse? = null,
    val role: String? = null,
    val status: String? = null,
    val ownerVerification: OwnerVerificationDto? = null,
)

@JsonClass(generateAdapter = true)
data class UserProfileImageResponse(val id: Long? = null, val imageUrl: String? = null)

@JsonClass(generateAdapter = true)
data class OwnerVerificationDto(
    val status: String? = null,
    val documentImage: UserProfileImageResponse? = null,
    val rejectReason: String? = null,
)

internal fun String?.normalizedImageUrl(): String? = this?.trim()?.takeIf {
    it.isNotBlank() && it != "default" && it != "defaultImage"
}

internal fun UserProfileResponseData.toInfo(): UserProfileInfo {
    val url = (profileImage?.imageUrl ?: profileImageUrl).normalizedImageUrl()
    return UserProfileInfo(
        id = userId ?: 0, email = email, nickname = nickname, profileImageUrl = url,
        snsProvider = snsProvider, role = role?.takeIf { it in listOf("USER", "OWNER", "ADMIN") } ?: "USER",
        status = status,
        profileImageFileId = profileImage?.id?.takeIf { it > 0 && url != null },
        ownerVerification = OwnerVerification(
            status = VerificationStatus.entries.firstOrNull { it.name == ownerVerification?.status } ?: VerificationStatus.NONE,
            documentUrl = ownerVerification?.documentImage?.imageUrl.normalizedImageUrl(),
            rejectReason = ownerVerification?.rejectReason?.trim()?.takeIf { it.isNotEmpty() },
        ),
    )
}
