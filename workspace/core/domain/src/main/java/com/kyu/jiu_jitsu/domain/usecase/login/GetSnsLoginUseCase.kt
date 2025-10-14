package com.kyu.jiu_jitsu.domain.usecase.login

import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.api.common.toUiError
import com.kyu.jiu_jitsu.data.model.dto.response.SnsLoginResponse
import com.kyu.jiu_jitsu.data.repository.SnsLoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSnsLoginUseCase @Inject constructor(
    private val loginRepository: SnsLoginRepository
) {
    suspend operator fun invoke(
        snsProvider: String,
        token: String
    ): Flow<UiState<SnsLoginResponse>> =
        loginRepository.getSnsLoginUserInfo(snsProvider, token).map { res ->
            when(res) {
                is ApiResult.Success -> UiState.Success(res.data)
                is ApiResult.Failure -> res.error.toUiError()
                else -> UiState.Loading
            }
        }

}