package com.kyu.jiu_jitsu.domain.usecase

import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.api.common.toUiError
import com.kyu.jiu_jitsu.data.model.RandomUserResponse
import com.kyu.jiu_jitsu.data.repository.RandomUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRandomUserUseCase @Inject constructor(
    private val randomUserRepository: RandomUserRepository
) {
    suspend operator fun invoke(resultCnt: Int): Flow<UiState<RandomUserResponse>> =
        randomUserRepository.getRandomUser(resultCnt).map { res ->
            when(res) {
                is ApiResult.Success -> UiState.Success(res.data)
                is ApiResult.Failure -> res.error.toUiError()
                else -> UiState.Loading
            }
        }

}