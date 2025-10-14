package com.kyu.jiu_jitsu.domain.usecase.user

import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.api.common.toUiError
import com.kyu.jiu_jitsu.data.model.UserProfileInfo
import com.kyu.jiu_jitsu.data.model.dto.DtoCommonCode
import com.kyu.jiu_jitsu.data.model.dto.response.toInfo
import com.kyu.jiu_jitsu.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        nickname: String,
        profileImageUrl: String,
    ): Flow<UiState<UserProfileInfo>> =
        userRepository.updateUserProfileInfo(
            nickname,
            profileImageUrl
        ).map { res ->
            when (res) {
                is ApiResult.Success -> {
                    if (res.data.success ?: false && res.data.code == DtoCommonCode.OK_CODE) {
                        UiState.Success(
                            res.data.data.toInfo()
                        )
                    }
                    else {
                        UiState.Error(res.data.message ?: "", false)
                    }
                }
                is ApiResult.Failure -> res.error.toUiError()
                else -> UiState.Loading

            }
        }

}