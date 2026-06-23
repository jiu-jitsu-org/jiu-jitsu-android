package com.kyu.jiu_jitsu.data.api

import com.kyu.jiu_jitsu.data.model.dto.request.RegisterImageRequest
import com.kyu.jiu_jitsu.data.model.dto.response.ImageAuthResponse
import com.kyu.jiu_jitsu.data.model.dto.response.RegisterImageResponse
import com.kyu.jiu_jitsu.data.utils.NetworkConfig
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
