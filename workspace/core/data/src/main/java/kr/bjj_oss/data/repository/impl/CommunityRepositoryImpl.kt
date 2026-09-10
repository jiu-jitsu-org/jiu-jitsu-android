package kr.bjj_oss.data.repository.impl

import kr.bjj_oss.data.api.CommunityService
import kr.bjj_oss.data.api.common.mapEnvelope
import kr.bjj_oss.data.api.common.safeApiCall
import kr.bjj_oss.data.model.dto.request.toRequest
import kr.bjj_oss.data.model.dto.response.toInfo
import kr.bjj_oss.data.repository.CommunityRepository
import kr.bjj_oss.model.AppResult
import kr.bjj_oss.model.CommunityProfileInfo
import kr.bjj_oss.model.CommunityProfileUpdate
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
