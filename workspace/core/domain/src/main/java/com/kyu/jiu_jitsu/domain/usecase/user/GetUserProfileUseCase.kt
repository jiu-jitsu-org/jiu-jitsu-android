package com.kyu.jiu_jitsu.domain.usecase.user

import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.model.UserProfileInfo
import com.kyu.jiu_jitsu.data.model.dto.response.toInfo
import com.kyu.jiu_jitsu.data.repository.UserRepository
import com.kyu.jiu_jitsu.domain.mapApiResponseToUiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<UiState<UserProfileInfo>> =
        userRepository.getUserProfileInfo()
            .mapApiResponseToUiState { data -> data.toInfo() }

}
