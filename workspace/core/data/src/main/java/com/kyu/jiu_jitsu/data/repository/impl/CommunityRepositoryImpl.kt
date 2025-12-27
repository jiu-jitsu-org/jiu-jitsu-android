package com.kyu.jiu_jitsu.data.repository.impl

import com.kyu.jiu_jitsu.data.api.CommunityService
import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.api.common.safeApiCall
import com.kyu.jiu_jitsu.data.model.dto.response.CommunityProfileData
import com.kyu.jiu_jitsu.data.model.dto.response.CommunityProfileResponse
import com.kyu.jiu_jitsu.data.repository.CommunityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(
    private val communityService: CommunityService
): CommunityRepository {

    override suspend fun getCommunityProfile(): Flow<ApiResult<CommunityProfileResponse>> = flow {
        emit(
            safeApiCall {
                communityService.reqCommunityProfile()
            }
        )
    }.flowOn(Dispatchers.IO)

    override suspend fun modifyCommunityProfile(body: CommunityProfileData): Flow<ApiResult<CommunityProfileResponse>> = flow {
        emit(
            safeApiCall {
                communityService.modifyCommunityProfile(body)
            }
        )
    }.flowOn(Dispatchers.IO)

}