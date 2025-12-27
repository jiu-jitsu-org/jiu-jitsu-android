package com.kyu.jiu_jitsu.domain.usecase.user

import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.api.common.toUiError
import com.kyu.jiu_jitsu.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CheckNickNameUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(nickname: String): Flow<UiState<Boolean>> =
        userRepository.checkNickname(nickname).map { res ->
            when(res) {
                is ApiResult.Success -> UiState.Success(res.data.success?:false)
                is ApiResult.Failure -> res.error.toUiError()
                else -> UiState.Loading
            }
        }
}