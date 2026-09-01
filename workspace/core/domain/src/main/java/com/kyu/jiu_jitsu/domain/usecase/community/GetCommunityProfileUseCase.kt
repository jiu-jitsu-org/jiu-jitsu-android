package com.kyu.jiu_jitsu.domain.usecase.community

import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.model.CommunityProfileInfo
import com.kyu.jiu_jitsu.data.model.dto.response.toInfo
import com.kyu.jiu_jitsu.data.repository.CommunityRepository
import com.kyu.jiu_jitsu.domain.mapApiResponseToUiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCommunityProfileUseCase @Inject constructor(
    private val communityRepository: CommunityRepository
) {
    operator fun invoke(): Flow<UiState<CommunityProfileInfo>> =
        communityRepository.getCommunityProfile()
            .mapApiResponseToUiState { data -> data.toInfo() }
}
