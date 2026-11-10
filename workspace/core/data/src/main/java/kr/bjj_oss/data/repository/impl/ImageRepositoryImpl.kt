package kr.bjj_oss.data.repository.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.ExifInterface
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kr.bjj_oss.data.api.ImageKitService
import kr.bjj_oss.data.api.ImageService
import kr.bjj_oss.data.api.UserService
import kr.bjj_oss.data.api.common.*
import kr.bjj_oss.data.model.dto.request.RegisterImageRequest
import kr.bjj_oss.data.model.dto.response.ApiResponse
import kr.bjj_oss.data.model.dto.response.toInfo
import kr.bjj_oss.data.repository.ImageRepository
import kr.bjj_oss.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.roundToInt

internal class ImageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageService: ImageService,
    private val imageKitService: ImageKitService,
    private val userService: UserService,
) : ImageRepository {
    override suspend fun uploadCommunityImage(imageUri: String, publicKey: String) =
        upload(imageUri, publicKey, verification = false)

    override suspend fun uploadVerificationImage(imageUri: String, publicKey: String) =
        upload(imageUri, publicKey, verification = true)

    override suspend fun deleteProfileImage(imageId: Long): AppResult<Unit> =
        safeApiCall {
            val response = imageService.deleteImage(imageId)
            if (!response.isSuccessful) throw HttpException(response)
        }.toAppResult { Unit }

    private suspend fun upload(imageUri: String, publicKey: String, verification: Boolean): AppResult<CdnImageInfo> =
        withContext(Dispatchers.IO) {
            val registered = safeApiCall {
                val bytes = jpeg(Uri.parse(imageUri), if (verification) 2560 else 1024)
                val prefix = if (verification) "verification" else "profile"
                val filename = "${prefix}_${System.currentTimeMillis() / 1000}.jpg"
                val auth = imageService.reqImageAuth().requiredData()
                val upload = imageKitService.uploadImage(
                    file = MultipartBody.Part.createFormData("file", filename, bytes.toRequestBody("image/jpeg".toMediaType())),
                    fileName = filename.plain(), publicKey = publicKey.plain(),
                    token = requireNotNull(auth.token).plain(),
                    signature = requireNotNull(auth.signature).plain(),
                    expire = requireNotNull(auth.expire).toString().plain(),
                    folder = "community/${if (verification) "verification" else "profile"}".plain(),
                    useUniqueFileName = "true".plain(),
                )
                imageService.registerImage(RegisterImageRequest(requireNotNull(upload.fileId), requireNotNull(upload.url)))
                    .requiredData().toInfo().also { require(it.id > 0 && it.imageUrl.isNotBlank()) }
            }.toAppResult { it }
            if (registered is AppResult.Failure) return@withContext registered
            val image = (registered as AppResult.Success).data
            val linked = safeApiCall {
                if (verification) userService.requestOwnerVerification(image.id).requiredData()
                else userService.updateUserProfileImage(image.id).requiredData()
            }.toAppResult { image }
            if (linked is AppResult.Failure) linked.copy(error = linked.error.copy(serverCode = "IMAGE_LINK")) else linked
        }

    private fun jpeg(uri: Uri, limit: Int): ByteArray {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)) { decoder, info, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                val scale = minOf(1f, limit.toFloat() / maxOf(info.size.width, info.size.height))
                decoder.setTargetSize((info.size.width * scale).roundToInt().coerceAtLeast(1), (info.size.height * scale).roundToInt().coerceAtLeast(1))
            }
        } else {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            val options = BitmapFactory.Options().apply {
                inSampleSize = 1
                while (maxOf(bounds.outWidth, bounds.outHeight) / inSampleSize > limit * 2) inSampleSize *= 2
            }
            val decoded = requireNotNull(context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) })
            val orientation = context.contentResolver.openInputStream(uri)?.use {
                ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            }
            val matrix = Matrix().apply {
                when (orientation) {
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> setScale(-1f, 1f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> setRotate(180f)
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> setScale(1f, -1f)
                    ExifInterface.ORIENTATION_TRANSPOSE -> { setRotate(90f); postScale(-1f, 1f) }
                    ExifInterface.ORIENTATION_ROTATE_90 -> setRotate(90f)
                    ExifInterface.ORIENTATION_TRANSVERSE -> { setRotate(-90f); postScale(-1f, 1f) }
                    ExifInterface.ORIENTATION_ROTATE_270 -> setRotate(-90f)
                }
            }
            Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true).also { if (it !== decoded) decoded.recycle() }
        }
        val scale = minOf(1f, limit.toFloat() / maxOf(bitmap.width, bitmap.height))
        val resized = if (scale < 1f) Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).roundToInt().coerceAtLeast(1), (bitmap.height * scale).roundToInt().coerceAtLeast(1), true) else bitmap
        return try {
            ByteArrayOutputStream().use { output ->
                check(resized.compress(Bitmap.CompressFormat.JPEG, 85, output))
                output.toByteArray()
            }
        } finally {
            if (resized !== bitmap) resized.recycle()
            bitmap.recycle()
        }
    }

    private fun String.plain() = toRequestBody("text/plain".toMediaType())
    private fun <T> ApiResponse<T>.requiredData(): T {
        if (success != true || data == null) throw ServerApiException(code, "Image operation failed")
        return requireNotNull(data)
    }
}
