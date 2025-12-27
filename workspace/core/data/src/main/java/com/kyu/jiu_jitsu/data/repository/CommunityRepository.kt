package com.kyu.jiu_jitsu.data.repository

import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.model.dto.response.CommunityProfileData
import com.kyu.jiu_jitsu.data.model.dto.response.CommunityProfileResponse
import kotlinx.coroutines.flow.Flow

interface CommunityRepository{

    suspend fun getCommunityProfile(): Flow<ApiResult<CommunityProfileResponse>>

    suspend fun modifyCommunityProfile(
        body: CommunityProfileData
    ): Flow<ApiResult<CommunityProfileResponse>>

}