package com.kyu.jiu_jitsu.domain.usecase

import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.api.common.toUiError
import com.kyu.jiu_jitsu.data.model.BootStrapInfo
import com.kyu.jiu_jitsu.data.model.dto.toInfo
import com.kyu.jiu_jitsu.data.repository.BootStrapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBootStrapInfoUseCase @Inject constructor(
    private val bootStrapRepository: BootStrapRepository
) {
    suspend operator fun invoke(): Flow<UiState<BootStrapInfo>> =
        bootStrapRepository.getBootStrapInfo().map { res ->
            when (res) {
                is ApiResult.Success -> {
                    if (res.data.success ?: false) {
                        UiState.Success(
                            res.data.data.toInfo()
                        )
                    } else {
                        UiState.Error("데이터 파싱 오류가 발생했어요.", false)
                    }
                }

                is ApiResult.Failure -> res.error.toUiError()
                else -> UiState.Loading
            }
        }
}