package kr.bjj_oss.data.api.common

import kr.bjj_oss.data.model.dto.DtoCommonCode
import kr.bjj_oss.data.model.dto.response.ApiResponse
import kr.bjj_oss.model.AppError
import kr.bjj_oss.model.AppErrorKind
import kr.bjj_oss.model.AppResult

/**
 * Converts a transport result into the stable result contract exposed by repositories.
 *
 * This is deliberately internal: Retrofit/Moshi failures are implementation details of
 * `core:data` and must not leak into feature or domain modules.
 */
internal inline fun <T, R> ApiResult<T>.toAppResult(
    transform: (T) -> R,
): AppResult<R> = when (this) {
    is ApiResult.Success -> AppResult.Success(transform(data))
    is ApiResult.Failure -> AppResult.Failure(error.toAppError())
}

/**
 * Validates the backend's envelope before mapping its nullable payload.
 *
 * Several endpoints return HTTP 200 even for business failures. Checking only the HTTP status
 * would therefore turn an error into a misleading empty success value.
 */
internal inline fun <T, R> ApiResult<ApiResponse<T>>.mapEnvelope(
    transform: (T) -> R,
): AppResult<R> = when (this) {
    is ApiResult.Failure -> AppResult.Failure(error.toAppError())
    is ApiResult.Success -> {
        val response = data
        val payload = response.data
        if (response.success == true && response.code == DtoCommonCode.OK_CODE && payload != null) {
            AppResult.Success(transform(payload))
        } else {
            AppResult.Failure(
                AppError(
                    kind = AppErrorKind.SERVER,
                    message = response.message,
                    serverCode = response.code,
                    retryable = false,
                ),
            )
        }
    }
}

/** Maps low-level failures while preserving the information useful for recovery and diagnostics. */
internal fun ApiError.toAppError(): AppError = when (this) {
    is ApiError.Network -> AppError(
        kind = AppErrorKind.NETWORK,
        message = cause.message,
        retryable = true,
    )

    is ApiError.Http -> AppError(
        kind = AppErrorKind.HTTP,
        message = body?.error ?: message,
        httpCode = code,
        retryable = code in 500..599,
    )

    is ApiError.Server -> AppError(
        kind = AppErrorKind.SERVER,
        message = message,
        serverCode = code,
    )

    is ApiError.Serialization -> AppError(
        kind = AppErrorKind.SERIALIZATION,
        message = cause.message,
    )

    is ApiError.Unknown -> AppError(
        kind = AppErrorKind.UNKNOWN,
        message = cause.message,
        retryable = true,
    )
}
