package kr.bjj_oss.data.repository.impl

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kr.bjj_oss.data.api.ImageKitService
import kr.bjj_oss.data.api.ImageService
import kr.bjj_oss.data.api.UserService
import kr.bjj_oss.data.api.common.ServerApiException
import kr.bjj_oss.data.api.common.mapEnvelope
import kr.bjj_oss.data.api.common.safeApiCall
import kr.bjj_oss.data.model.dto.DtoCommonCode
import kr.bjj_oss.data.model.dto.request.RegisterImageRequest
import kr.bjj_oss.data.model.dto.response.toInfo
import kr.bjj_oss.data.repository.ImageRepository
import kr.bjj_oss.model.AppResult
import kr.bjj_oss.model.CdnImageInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class ImageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageService: ImageService,
    private val imageKitService: ImageKitService,
    private val userService: UserService,
) : ImageRepository {

    override suspend fun uploadCommunityImage(
        imageUri: String,
        publicKey: String,
    ): AppResult<CdnImageInfo> = withContext(Dispatchers.IO) {
        safeApiCall {
                val uri = Uri.parse(imageUri)
                val imageFileName = uri.createImageKitFileName()

                // 1. 내 서버에서 ImageKit 업로드에 필요한 일회성 인증값을 먼저 가져온다.
                // /api/image/auth 응답은 공통 래퍼(success/code/message/data) 형태이며,
                // 실패 응답은 HTTP 200이어도 success=false 이거나 data가 null일 수 있다.
                // 따라서 ImageKit에 업로드하기 전에 서버 성공 코드와 data 존재 여부를 명시적으로 검증한다.
                // 이 API는 토큰 포함 Retrofit을 사용하므로 A0003 refresh 공통 처리 대상이다.
                val authResponse = imageService.reqImageAuth()
                if (authResponse.success != true || authResponse.code != DtoCommonCode.OK_CODE) {
                    throw ServerApiException(
                        code = authResponse.code,
                        message = authResponse.message ?: DEFAULT_IMAGE_AUTH_ERROR_MESSAGE,
                    )
                }
                val auth = authResponse.data ?: throw ServerApiException(
                    code = authResponse.code,
                    message = authResponse.message ?: DEFAULT_IMAGE_AUTH_ERROR_MESSAGE,
                )
                val uploadToken = auth.token ?: throw ServerApiException(
                    code = authResponse.code,
                    message = "ImageKit token is null.",
                )
                val signature = auth.signature ?: throw ServerApiException(
                    code = authResponse.code,
                    message = "ImageKit signature is null.",
                )
                val expire = auth.expire ?: throw ServerApiException(
                    code = authResponse.code,
                    message = "ImageKit expire is null.",
                )

                // 2. 선택된 Uri를 RequestBody로 감싸 ImageKit에 직접 multipart 업로드한다.
                // readBytes()로 전체 이미지를 메모리에 올리지 않고, 업로드 시점에 ContentResolver stream을 sink로 복사한다.
                val uploadResponse = imageKitService.uploadImage(
                    file = uri.toMultipartFile(imageFileName),
                    fileName = imageFileName.toPlainRequestBody(),
                    publicKey = publicKey.toPlainRequestBody(),
                    token = uploadToken.toPlainRequestBody(),
                    signature = signature.toPlainRequestBody(),
                    expire = expire.toString().toPlainRequestBody(),
                    folder = IMAGE_KIT_COMMUNITY_FOLDER.toPlainRequestBody(),
                )
                val cdnId = requireNotNull(uploadResponse.fileId) { "ImageKit fileId is null." }
                val imageUrl = requireNotNull(uploadResponse.url) { "ImageKit url is null." }

                // 3. CDN 업로드가 성공한 뒤에만 내 서버 DB에 이미지 정보를 TEMP 상태로 등록한다.
                // /api/image 응답도 공통 래퍼(success/code/message/data) 형태이므로,
                // success=false 이거나 data=null이면 서버 message를 보존한 실패로 변환한다.
                // 다음 단계의 사용자 프로필 이미지 업데이트는 이 응답의 data.id를 request parameter로 사용한다.
                val registerResponse = imageService.registerImage(
                    RegisterImageRequest(
                        cdnId = cdnId,
                        imageUrl = imageUrl,
                    )
                )
                if (registerResponse.success != true || registerResponse.code != DtoCommonCode.OK_CODE) {
                    throw ServerApiException(
                        code = registerResponse.code,
                        message = registerResponse.message ?: DEFAULT_REGISTER_IMAGE_ERROR_MESSAGE,
                    )
                }
                if (registerResponse.data == null) {
                    throw ServerApiException(
                        code = registerResponse.code,
                        message = registerResponse.message ?: DEFAULT_REGISTER_IMAGE_ERROR_MESSAGE,
                    )
                }
                val registeredImageId = registerResponse.data.id ?: throw ServerApiException(
                    code = registerResponse.code,
                    message = DEFAULT_REGISTER_IMAGE_ID_ERROR_MESSAGE,
                )

                // 4. 등록된 이미지 id를 사용자 프로필 이미지로 반영한다.
                // 서버 요구사항이 request parameter 전달이므로 PUT /api/user/profile/image?imageFileId={id}
                // 형태로 호출한다. 이 API 또한 공통 래퍼 응답이므로, HTTP 200이어도 success=false 또는
                // data=null이면 최종 프로필 반영이 실패한 것으로 보고 전체 업로드 플로우를 실패 처리한다.
                val userProfileResponse = userService.updateUserProfileImage(
                    imageId = registeredImageId,
                )
                if (userProfileResponse.success != true || userProfileResponse.code != DtoCommonCode.OK_CODE) {
                    throw ServerApiException(
                        code = userProfileResponse.code,
                        message = userProfileResponse.message ?: DEFAULT_UPDATE_PROFILE_IMAGE_ERROR_MESSAGE,
                    )
                }
                if (userProfileResponse.data == null) {
                    throw ServerApiException(
                        code = userProfileResponse.code,
                        message = userProfileResponse.message ?: DEFAULT_UPDATE_PROFILE_IMAGE_ERROR_MESSAGE,
                    )
                }

                // The repository maps this DTO to CdnImageInfo below. The UI therefore observes
                // only stable app data, while successful profile activation is guaranteed above.
                registerResponse
        }.mapEnvelope { response -> response.toInfo() }
    }

    private fun Uri.toMultipartFile(fileName: String): MultipartBody.Part {
        val mimeType = context.contentResolver.getType(this) ?: DEFAULT_IMAGE_MIME_TYPE
        val requestBody = UriRequestBody(
            context = context,
            uri = this,
            contentType = mimeType,
        )
        return MultipartBody.Part.createFormData(
            name = IMAGE_KIT_FILE_PART_NAME,
            filename = fileName,
            body = requestBody,
        )
    }

    private fun Uri.createImageKitFileName(): String {
        val originName = displayName()?.sanitizeFileName()
            ?.takeIf(String::isNotBlank)
            ?: DEFAULT_IMAGE_FILE_NAME
        val timestamp = LocalDateTime.now().format(FILE_NAME_TIMESTAMP_FORMATTER)
        return "${timestamp}_$originName"
    }

    private fun Uri.displayName(): String? {
        val cursor = context.contentResolver.query(
            this,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null,
        )
        return cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) it.getString(nameIndex) else null
            } else {
                null
            }
        }
    }

    private fun String.sanitizeFileName(): String =
        replace(Regex("[^A-Za-z0-9._-]"), "_")

    private fun String.toPlainRequestBody(): RequestBody =
        toRequestBody(TEXT_PLAIN_MEDIA_TYPE.toMediaTypeOrNull())

    private class UriRequestBody(
        private val context: Context,
        private val uri: Uri,
        private val contentType: String,
    ) : RequestBody() {

        override fun contentType() = contentType.toMediaTypeOrNull()

        override fun contentLength(): Long =
            runCatching {
                context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { descriptor ->
                    descriptor.length
                } ?: -1L
            }.getOrDefault(-1L)

        override fun writeTo(sink: BufferedSink) {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                while (true) {
                    val read = input.read(buffer)
                    if (read == -1) break
                    sink.write(buffer, 0, read)
                }
            } ?: throw IllegalStateException("Cannot open image input stream.")
        }
    }

    private companion object {
        private const val IMAGE_KIT_COMMUNITY_FOLDER = "community"
        private const val IMAGE_KIT_FILE_PART_NAME = "file"
        private const val DEFAULT_IMAGE_MIME_TYPE = "image/jpeg"
        private const val DEFAULT_IMAGE_FILE_NAME = "profile_image.jpg"
        private const val DEFAULT_IMAGE_AUTH_ERROR_MESSAGE = "ImageKit auth data is null."
        private const val DEFAULT_REGISTER_IMAGE_ERROR_MESSAGE = "Registered image data is null."
        private const val DEFAULT_REGISTER_IMAGE_ID_ERROR_MESSAGE = "Registered image id is null."
        private const val DEFAULT_UPDATE_PROFILE_IMAGE_ERROR_MESSAGE = "User profile image update failed."
        private const val TEXT_PLAIN_MEDIA_TYPE = "text/plain"
        private val FILE_NAME_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")
    }
}
