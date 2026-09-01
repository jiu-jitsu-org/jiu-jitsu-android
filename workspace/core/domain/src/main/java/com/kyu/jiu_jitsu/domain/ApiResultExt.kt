package com.kyu.jiu_jitsu.domain

import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.api.common.toUiError
import com.kyu.jiu_jitsu.data.model.dto.DtoCommonCode
import com.kyu.jiu_jitsu.data.model.dto.response.ApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <T, R> ApiResult<T>.toUiState(
    transform: (T) -> R,
): UiState<R> =
    when (this) {
        is ApiResult.Success -> UiState.Success(transform(data))
        is ApiResult.Failure -> error.toUiError()
    }

inline fun <D, R, T : ApiResponse<D>> ApiResult<T>.toApiResponseUiState(
    successCode: String = DtoCommonCode.OK_CODE,
    transform: (D?) -> R,
): UiState<R> =
    when (this) {
        is ApiResult.Success -> data.toUiState(successCode, transform)
        is ApiResult.Failure -> error.toUiError()
    }

inline fun <T, R> Flow<ApiResult<T>>.mapResultToUiState(
    crossinline transform: (T) -> R,
): Flow<UiState<R>> =
    map { result -> result.toUiState(transform) }

inline fun <D, R, T : ApiResponse<D>> Flow<ApiResult<T>>.mapApiResponseToUiState(
    successCode: String = DtoCommonCode.OK_CODE,
    crossinline transform: (D?) -> R,
): Flow<UiState<R>> =
    map { result -> result.toApiResponseUiState(successCode, transform) }

inline fun <D, R> ApiResponse<D>.toUiState(
    successCode: String = DtoCommonCode.OK_CODE,
    transform: (D?) -> R,
): UiState<R> =
    if (success == true && code == successCode) {
        UiState.Success(transform(data))
    } else {
        UiState.Error(message = message ?: "", retryable = false)
    }
