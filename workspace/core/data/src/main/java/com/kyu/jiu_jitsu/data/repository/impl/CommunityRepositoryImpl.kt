package com.kyu.jiu_jitsu.data.repository.impl

import com.kyu.jiu_jitsu.data.api.CommunityService
import com.kyu.jiu_jitsu.data.api.common.mapEnvelope
import com.kyu.jiu_jitsu.data.api.common.safeApiCall
import com.kyu.jiu_jitsu.data.model.dto.request.toRequest
import com.kyu.jiu_jitsu.data.model.dto.response.toInfo
import com.kyu.jiu_jitsu.data.repository.CommunityRepository
import com.kyu.jiu_jitsu.model.AppResult
import com.kyu.jiu_jitsu.model.CommunityProfileInfo
import com.kyu.jiu_jitsu.model.CommunityProfileUpdate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

internal class CommunityRepositoryImpl @Inject constructor(
    private val communityService: CommunityService
): CommunityRepository {

    private val mutableCommunityProfile = MutableStateFlow<CommunityProfileInfo?>(null)
    override val communityProfile: StateFlow<CommunityProfileInfo?> =
        mutableCommunityProfile.asStateFlow()

    override suspend fun getCommunityProfile(): AppResult<CommunityProfileInfo> =
        safeApiCall { communityService.reqCommunityProfile() }
            .mapEnvelope { response -> response.toInfo() }
            .also(::cacheSuccessfulProfile)

    override suspend fun modifyCommunityProfile(
        update: CommunityProfileUpdate,
    ): AppResult<CommunityProfileInfo> =
        safeApiCall { communityService.modifyCommunityProfile(update.toRequest()) }
            .mapEnvelope { response -> response.toInfo() }
            .also(::cacheSuccessfulProfile)

    override fun clearCachedProfile() {
        mutableCommunityProfile.value = null
    }

    /** Only a successful server snapshot may replace the current source of truth. */
    private fun cacheSuccessfulProfile(result: AppResult<CommunityProfileInfo>) {
        if (result is AppResult.Success) mutableCommunityProfile.value = result.data
    }
}
