package com.kyu.jiu_jitsu.domain.usecase.community

import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.model.CommunityProfileInfo
import com.kyu.jiu_jitsu.data.model.dto.request.UpdateCommunityProfileRequest
import com.kyu.jiu_jitsu.data.model.dto.response.toInfo
import com.kyu.jiu_jitsu.data.repository.CommunityRepository
import com.kyu.jiu_jitsu.domain.mapApiResponseToUiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateCommunityProfileUseCase @Inject constructor(
    private val communityRepository: CommunityRepository
) {
    suspend operator fun invoke(updateCommunityProfileRequest: UpdateCommunityProfileRequest): Flow<UiState<CommunityProfileInfo>> =
        communityRepository.modifyCommunityProfile(updateCommunityProfileRequest)
            .mapApiResponseToUiState { data -> data.toInfo() }

}
