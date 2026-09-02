package com.kyu.jiu_jitsu.data.api.common

sealed interface ApiError {
    data class Http(
        val code: Int?,
        val message: String?,
        val body: ServerError?
    ) : ApiError

    data class Server(
        val code: String?,
        val message: String?,
    ) : ApiError

    data class Network(val cause: java.io.IOException) : ApiError
    data class Serialization(val cause: com.squareup.moshi.JsonDataException) : ApiError
    data class Unknown(val cause: Throwable) : ApiError
}

class ServerApiException(
    val code: String?,
    message: String?,
) : RuntimeException(message)
