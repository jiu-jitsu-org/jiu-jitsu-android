package com.kyu.jiu_jitsu.data.api.common

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import okhttp3.ResponseBody
import retrofit2.HttpException
import kotlin.coroutines.cancellation.CancellationException

suspend inline fun <T> safeApiCall(
    crossinline block: suspend () -> T
): ApiResult<T> = try {
    ApiResult.Success(block())
} catch (e: HttpException) {
    val errorBody = e.response()?.errorBody()?.use(ResponseBody::string)
//    val parsed = errorBody?.let {
//        runCatching { moshi.adapter(ServerError::class.java).fromJson(it) }.getOrNull()
//    }
//    ApiResult.Failure(ApiError.Http(e.code(), e.message(), parsed))
    ApiResult.Failure(ApiError.Http(e.code(), e.message(), null))
} catch (e: java.io.IOException) {
    ApiResult.Failure(ApiError.Network(e))
} catch (e: JsonDataException) {
    ApiResult.Failure(ApiError.Serialization(e))
} catch (t: Throwable) {
    // 코루틴 취소는 그대로 전파
    if (t is CancellationException) throw t
    ApiResult.Failure(ApiError.Unknown(t))
}