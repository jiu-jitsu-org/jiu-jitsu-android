package com.kyu.jiu_jitsu.domain.usecase.login

import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.model.LoginInfo
import com.kyu.jiu_jitsu.data.model.dto.response.toInfo
import com.kyu.jiu_jitsu.data.repository.SnsLoginRepository
import com.kyu.jiu_jitsu.domain.mapResultToUiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSnsLoginUseCase @Inject constructor(
    private val loginRepository: SnsLoginRepository
) {
    suspend operator fun invoke(
        snsProvider: String,
        token: String
    ): Flow<UiState<LoginInfo>> =
        loginRepository.getSnsLoginUserInfo(snsProvider, token)
            .mapResultToUiState { response -> response.data.toInfo() }

}
