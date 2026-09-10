package kr.bjj_oss.data.api

import kr.bjj_oss.data.model.dto.request.RegisterImageRequest
import kr.bjj_oss.data.model.dto.response.ImageAuthResponse
import kr.bjj_oss.data.model.dto.response.RegisterImageResponse
import kr.bjj_oss.data.utils.NetworkConfig
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ImageService {

    @GET(NetworkConfig.Image.AUTH)
    suspend fun reqImageAuth(): ImageAuthResponse

    @POST(NetworkConfig.Image.IMAGE)
    suspend fun registerImage(
        @Body request: RegisterImageRequest
    ): RegisterImageResponse
}
