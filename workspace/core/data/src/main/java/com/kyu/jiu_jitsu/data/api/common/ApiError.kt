package com.kyu.jiu_jitsu.data.api.common

sealed interface ApiError {
    data class Http(
        val code: Int,
        val message: String?,
        val body: ServerError?
    ) : ApiError

    data class Network(val cause: java.io.IOException) : ApiError
    data class Serialization(val cause: com.squareup.moshi.JsonDataException) : ApiError
    data class Unknown(val cause: Throwable) : ApiError
}

fun ApiError.toUiError(): UiState.Error = when (this) {
    is ApiError.Network       -> UiState.Error(message = "네트워크 연결을 확인해주세요.", retryable =  true)
    is ApiError.Http          -> UiState.Error(code, body?.error ?: "서버 오류($code)", code in 500..599)
    is ApiError.Serialization -> UiState.Error(message = "데이터 파싱 오류가 발생했어요.", retryable = false)
    is ApiError.Unknown       -> UiState.Error(message = "알 수 없는 오류가 발생했어요.", retryable = true)
}