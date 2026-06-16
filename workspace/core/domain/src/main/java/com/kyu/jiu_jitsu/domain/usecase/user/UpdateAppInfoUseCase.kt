package com.kyu.jiu_jitsu.domain.usecase.user

import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.repository.UserRepository
import com.kyu.jiu_jitsu.domain.mapResultToUiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateAppInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        fcmToken: String,
        deviceId: String,
        osVersion: String
    ): Flow<UiState<Boolean>> =
        userRepository.appInfo(fcmToken, deviceId, osVersion)
            .mapResultToUiState { true }
}
