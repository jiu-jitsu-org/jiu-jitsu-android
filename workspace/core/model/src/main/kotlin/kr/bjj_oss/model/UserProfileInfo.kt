package kr.bjj_oss.model

/** Stable app representation of the authenticated user's account profile. */
data class UserProfileInfo(
    val id: Int?,
    val email: String?,
    val nickname: String?,
    val profileImageUrl: String?,
    val snsProvider: String?,
    val role: String?,
    val status: String?,
    val profileImageFileId: Long? = null,
    val ownerVerification: OwnerVerification = OwnerVerification(),
)

enum class VerificationStatus { NONE, PENDING, APPROVED, REJECTED }
data class OwnerVerification(
    val status: VerificationStatus = VerificationStatus.NONE,
    val documentUrl: String? = null,
    val rejectReason: String? = null,
)
