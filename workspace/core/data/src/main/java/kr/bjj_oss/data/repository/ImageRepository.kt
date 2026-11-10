package kr.bjj_oss.data.repository

import kr.bjj_oss.model.AppResult
import kr.bjj_oss.model.CdnImageInfo

/** Coordinates CDN upload, backend registration, and profile-image activation. */
interface ImageRepository {

    suspend fun uploadVerificationImage(imageUri: String, publicKey: String): AppResult<CdnImageInfo>
    suspend fun deleteProfileImage(imageId: Long): AppResult<Unit>

    suspend fun uploadCommunityImage(
        imageUri: String,
        publicKey: String,
    ): AppResult<CdnImageInfo>
}
