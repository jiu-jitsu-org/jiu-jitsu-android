package kr.bjj_oss.data.session

import kr.bjj_oss.data.BuildConfig
import kr.bjj_oss.data.model.dto.request.RefreshTokenRequest
import kr.bjj_oss.data.model.dto.response.SnsLoginResponse
import kr.bjj_oss.data.utils.NetworkConfig
import kr.bjj_oss.model.AppError
import kr.bjj_oss.model.AppErrorKind
import kr.bjj_oss.model.AppResult
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefreshCoordinator @Inject constructor(private val store: SessionLocalDataSource, moshi: Moshi) {
    // Credential-only client deliberately has no profiler, auth interceptor, retry or redirect.
    private val client = OkHttpClient.Builder().followRedirects(false).followSslRedirects(false)
        .retryOnConnectionFailure(false).callTimeout(8, TimeUnit.SECONDS).build()
    private val requestAdapter = moshi.adapter(RefreshTokenRequest::class.java)
    private val responseAdapter = moshi.adapter(SnsLoginResponse::class.java)
    private val url = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
        .addPathSegments(NetworkConfig.Authentication.REFRESH).build()
    private val gate = RefreshGate(store::tokenSnapshot, ::fetch, store::updateTokensIfCurrent)

    val revision get() = store.revision.value
    suspend fun refresh(expiredAccessToken: String?, expectedRevision: Long = revision): AppResult<String> =
        gate.refresh(expiredAccessToken, expectedRevision)

    private suspend fun fetch(refreshToken: String): AppResult<Pair<String, String>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).post(
                requestAdapter.toJson(RefreshTokenRequest(refreshToken)).toRequestBody("application/json".toMediaType()),
            ).build()
            client.newCall(request).execute().use { response ->
                val envelope = response.body?.string()?.let { responseAdapter.fromJson(it) }
                val data = envelope?.data
                if (!response.isSuccessful || envelope?.success != true || data?.accessToken.isNullOrBlank() || data?.refreshToken.isNullOrBlank()) {
                    AppResult.Failure(AppError(AppErrorKind.SERVER))
                } else AppResult.Success(requireNotNull(data?.accessToken) to requireNotNull(data?.refreshToken))
            }
        } catch (_: IOException) {
            AppResult.Failure(AppError(AppErrorKind.NETWORK, retryable = true))
        } catch (_: com.squareup.moshi.JsonDataException) {
            AppResult.Failure(AppError(AppErrorKind.SERIALIZATION))
        }
    }
}
