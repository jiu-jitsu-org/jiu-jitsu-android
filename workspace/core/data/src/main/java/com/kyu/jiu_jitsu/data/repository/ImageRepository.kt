package com.kyu.jiu_jitsu.data.repository

import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.model.dto.response.RegisterImageResponse
import kotlinx.coroutines.flow.Flow

interface ImageRepository {

    suspend fun uploadCommunityImage(
        imageUri: String,
        publicKey: String,
    ): Flow<ApiResult<RegisterImageResponse>>
}
