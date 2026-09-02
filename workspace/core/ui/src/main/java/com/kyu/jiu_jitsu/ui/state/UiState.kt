package com.kyu.jiu_jitsu.ui.state

import com.kyu.jiu_jitsu.model.AppError
import com.kyu.jiu_jitsu.model.AppErrorKind
import com.kyu.jiu_jitsu.model.AppResult

/**
 * Small shared presentation contract for screens that render one asynchronous operation.
 *
 * Feature-specific screens should define their own richer state when they need multiple pieces of
 * data. This type intentionally lives in `core:ui`, not `core:data`, because loading and retry UI
 * are presentation concerns.
 */
sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val result: T) : UiState<T>
    data class Error(
        val code: Int? = null,
        val serverCode: String? = null,
        val message: String,
        val retryable: Boolean,
    ) : UiState<Nothing>
}

/** Converts an app result at the ViewModel boundary, after repository work has completed. */
fun <T> AppResult<T>.toUiState(): UiState<T> = when (this) {
    is AppResult.Success -> UiState.Success(data)
    is AppResult.Failure -> error.toUiError()
}

/** Selects safe user-facing fallbacks without exposing transport exceptions to UI code. */
fun AppError.toUiError(): UiState.Error {
    val fallbackMessage = when (kind) {
        AppErrorKind.NETWORK -> "네트워크 연결을 확인해주세요."
        AppErrorKind.HTTP -> "서버 오류가 발생했어요."
        AppErrorKind.SERVER -> "요청을 처리하지 못했어요."
        AppErrorKind.SERIALIZATION -> "데이터를 불러오지 못했어요."
        AppErrorKind.UNKNOWN -> "알 수 없는 오류가 발생했어요."
    }
    return UiState.Error(
        code = httpCode,
        serverCode = serverCode,
        message = message?.takeIf(String::isNotBlank) ?: fallbackMessage,
        retryable = retryable,
    )
}
