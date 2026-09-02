package com.kyu.jiu_jitsu.model

/**
 * Stable result contract exposed by repositories and reusable operations.
 *
 * Transport-specific exceptions are converted to this type inside the data layer. Keeping the
 * result free of Retrofit, Moshi, and Android classes lets ViewModels and domain code react to
 * failures without learning how the data was fetched.
 */
sealed interface AppResult<out T> {

    /** A completed operation whose value is safe to expose outside the data layer. */
    data class Success<T>(val data: T) : AppResult<T>

    /** A failed operation represented by an app-level error rather than a transport exception. */
    data class Failure(val error: AppError) : AppResult<Nothing>
}

/**
 * Error information that is meaningful across data, domain, and presentation boundaries.
 *
 * [message] may contain a backend-provided explanation, but it is not guaranteed to be suitable
 * for direct display. The presentation layer decides the final localized message from [kind].
 */
data class AppError(
    val kind: AppErrorKind,
    val message: String? = null,
    val httpCode: Int? = null,
    val serverCode: String? = null,
    val retryable: Boolean = false,
)

/** Coarse categories used by presentation code to choose user-facing recovery behavior. */
enum class AppErrorKind {
    NETWORK,
    HTTP,
    SERVER,
    SERIALIZATION,
    UNKNOWN,
}
