package com.kyu.jiu_jitsu.domain.usecase

import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.model.BootStrapInfo
import com.kyu.jiu_jitsu.data.model.dto.response.toInfo
import com.kyu.jiu_jitsu.data.repository.BootStrapRepository
import com.kyu.jiu_jitsu.domain.mapApiResponseToUiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBootStrapInfoUseCase @Inject constructor(
    private val bootStrapRepository: BootStrapRepository
) {
    suspend operator fun invoke(): Flow<UiState<BootStrapInfo>> =
        bootStrapRepository.getBootStrapInfo()
            .mapApiResponseToUiState { data -> data.toInfo() }
}
