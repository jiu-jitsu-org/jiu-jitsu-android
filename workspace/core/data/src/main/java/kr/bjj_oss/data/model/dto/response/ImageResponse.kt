package kr.bjj_oss.data.model.dto.response

import kr.bjj_oss.model.CdnImageInfo
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageAuthResponse(
    override val success: Boolean?,
    override val code: String?,
    override val message: String?,
    override val data: ImageAuthData?,
) : ApiResponse<ImageAuthData>

@JsonClass(generateAdapter = true)
data class ImageAuthData(
    val token: String?,
    val expire: Long?,
    val signature: String?,
)

@JsonClass(generateAdapter = true)
data class ImageKitUploadResponse(
    val fileId: String?,
    val url: String?,
)

@JsonClass(generateAdapter = true)
data class RegisterImageResponse(
    override val success: Boolean?,
    override val code: String?,
    override val message: String?,
    override val data: RegisterImageData?,
) : ApiResponse<RegisterImageData>

@JsonClass(generateAdapter = true)
data class RegisterImageData(
    val id: Int?,
    val cdnId: String?,
    val imageUrl: String?,
    val status: String?,
)

internal fun RegisterImageData.toInfo(): CdnImageInfo =
    CdnImageInfo(
        id = id ?: 0,
        cdnId = cdnId.orEmpty(),
        imageUrl = imageUrl.orEmpty(),
        status = status.orEmpty(),
    )
