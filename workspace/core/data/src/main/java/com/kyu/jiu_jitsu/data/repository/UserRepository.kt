package com.kyu.jiu_jitsu.data.repository

import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.model.dto.request.UpdateProfileRequest
import com.kyu.jiu_jitsu.data.model.dto.response.UserProfileResponse
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun getUserProfileInfo(): Flow<ApiResult<UserProfileResponse>>

    suspend fun updateUserProfileInfo(
        nickname: String,
        profileImageUrl: String,
    ): Flow<ApiResult<UserProfileResponse>>

}