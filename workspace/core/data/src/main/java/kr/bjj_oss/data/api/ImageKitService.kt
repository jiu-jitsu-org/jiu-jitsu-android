package kr.bjj_oss.data.api

import kr.bjj_oss.data.model.dto.response.ImageKitUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageKitService {

    @Multipart
    @POST("api/v1/files/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("fileName") fileName: RequestBody,
        @Part("publicKey") publicKey: RequestBody,
        @Part("token") token: RequestBody,
        @Part("signature") signature: RequestBody,
        @Part("expire") expire: RequestBody,
        @Part("folder") folder: RequestBody,
    ): ImageKitUploadResponse
}
