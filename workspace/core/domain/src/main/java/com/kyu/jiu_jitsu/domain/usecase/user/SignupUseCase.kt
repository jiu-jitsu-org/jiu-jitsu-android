package com.kyu.jiu_jitsu.domain.usecase.user

import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.model.LoginInfo
import com.kyu.jiu_jitsu.data.model.dto.response.toInfo
import com.kyu.jiu_jitsu.data.repository.UserRepository
import com.kyu.jiu_jitsu.domain.mapResultToUiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        nickName: String,
        isMarketingAgreed: Boolean,
    ): Flow<UiState<LoginInfo>> =
        userRepository.signupUser(nickName, isMarketingAgreed)
            .mapResultToUiState { response -> response.data.toInfo() }
}
