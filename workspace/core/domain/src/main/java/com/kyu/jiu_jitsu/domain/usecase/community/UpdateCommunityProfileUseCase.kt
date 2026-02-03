package com.kyu.jiu_jitsu.domain.usecase.community

import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.api.common.toUiError
import com.kyu.jiu_jitsu.data.model.CommunityProfileInfo
import com.kyu.jiu_jitsu.data.model.dto.DtoCommonCode.OK_CODE
import com.kyu.jiu_jitsu.data.model.dto.request.UpdateCommunityProfileRequest
import com.kyu.jiu_jitsu.data.model.dto.response.toInfo
import com.kyu.jiu_jitsu.data.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UpdateCommunityProfileUseCase @Inject constructor(
    private val communityRepository: CommunityRepository
) {
    suspend operator fun invoke(updateCommunityProfileRequest: UpdateCommunityProfileRequest): Flow<UiState<CommunityProfileInfo>> =
        communityRepository.modifyCommunityProfile(updateCommunityProfileRequest).map { res ->
            when(res) {
                is ApiResult.Success -> {
                    if (res.data.success ?: false && res.data.code == OK_CODE) {
                        UiState.Success(res.data.data.toInfo())
                    } else {
                        UiState.Error(message =  res.data.message ?: "", retryable =  false)
                    }

                }
                is ApiResult.Failure -> res.error.toUiError()
                else -> UiState.Loading

            }
        }

}