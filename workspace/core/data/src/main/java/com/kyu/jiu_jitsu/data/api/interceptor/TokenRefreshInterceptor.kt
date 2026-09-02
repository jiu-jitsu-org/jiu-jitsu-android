package com.kyu.jiu_jitsu.data.api.interceptor

import com.kyu.jiu_jitsu.data.model.dto.request.RefreshTokenRequest
import com.kyu.jiu_jitsu.data.model.dto.response.SnsLoginResponse
import com.kyu.jiu_jitsu.data.session.SessionLocalDataSource
import com.kyu.jiu_jitsu.data.utils.NetworkConfig
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

/**
 * 토큰 포함 REST API의 공통 재인증 처리 인터셉터.
 *
 * 서버가 HTTP 200과 함께 아래와 같은 비즈니스 에러를 내려주는 경우가 있으므로,
 * Retrofit 변환 단계까지 보내기 전에 OkHttp 응답 바디를 먼저 확인한다.
 *
 * {
 *   "success": false,
 *   "code": "A0003",
 *   "message": "로그인이 필요한 서비스입니다. 로그인을 해주세요.",
 *   "data": null
 * }
 *
 * 처리 순서:
 * 1. 원 REST API 응답 바디에서 code == A0003 여부를 확인한다.
 * 2. A0003이면 로컬에 저장된 refreshToken으로 POST /auth/refresh를 호출한다.
 * 3. refresh 응답의 accessToken, refreshToken을 암호화 저장소에 원자적으로 저장한다.
 * 4. 세션 데이터 소스가 동기식 요청 헤더용 메모리 토큰도 함께 갱신한다.
 * 5. 저장이 끝난 뒤 원 요청을 새 Authorization 헤더로 1회만 재호출한다.
 */
class TokenRefreshInterceptor @Inject constructor(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val refreshClient: OkHttpClient,
    moshi: Moshi,
    baseUrl: String,
) : Interceptor {

    private val refreshUrl = baseUrl.toHttpUrl()
        .newBuilder()
        .addPathSegments(NetworkConfig.Authentication.REFRESH)
        .build()
    private val errorAdapter: JsonAdapter<TokenErrorResponse> = moshi.adapter(TokenErrorResponse::class.java)
    private val refreshRequestAdapter: JsonAdapter<RefreshTokenRequest> = moshi.adapter(RefreshTokenRequest::class.java)
    private val refreshResponseAdapter: JsonAdapter<SnsLoginResponse> = moshi.adapter(SnsLoginResponse::class.java)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val responseBodySnapshot = response.bodySnapshot()

        // refresh 후 재시도한 요청까지 A0003이면 refresh token 자체가 만료된 상태일 가능성이 높다.
        // 이 경우 무한 루프를 막기 위해 추가 refresh 없이 서버 응답을 그대로 상위 계층으로 전달한다.
        if (responseBodySnapshot == null || !responseBodySnapshot.isLoginRequiredError() || request.isTokenRefreshRetry()) {
            return response
        }

        val requestAccessToken = request.accessTokenFromHeader()

        val refreshResult = synchronized(refreshLock) {
            val latestAccessToken = sessionLocalDataSource.currentAccessToken()

            // 동시에 여러 API가 A0003을 받으면 첫 번째 요청만 refresh를 수행한다.
            // 락 대기 중 다른 요청이 이미 토큰을 갱신했다면, 현재 요청은 저장된 최신 accessToken으로 바로 재시도한다.
            if (!latestAccessToken.isNullOrBlank() && latestAccessToken != requestAccessToken) {
                TokenRefreshResult.Success(latestAccessToken)
            } else {
                refreshToken()
            }
        }

        val retryAccessToken = (refreshResult as? TokenRefreshResult.Success)?.accessToken
        if (retryAccessToken.isNullOrBlank()) {
            // refresh 실패 시 원래 A0003 응답을 유지해야 Retrofit/도메인 계층이 기존 에러 흐름대로 처리할 수 있다.
            // 다만 원 응답은 아래에서 close 하므로, peek 해둔 문자열로 동일한 응답 바디를 다시 구성한다.
            val rebuiltResponse = response.rebuildWithBody(responseBodySnapshot)
            response.close()
            return rebuiltResponse
        }

        response.close()
        return chain.proceed(
            request.newBuilder()
                .header(AUTHORIZATION_HEADER, "$BEARER_PREFIX$retryAccessToken")
                .tag(TokenRefreshRetryMarker::class.java, TokenRefreshRetryMarker)
                .build()
        )
    }

    private fun refreshToken(): TokenRefreshResult {
        val savedRefreshToken = runBlocking { sessionLocalDataSource.refreshToken() }
        if (savedRefreshToken.isNullOrBlank()) {
            return TokenRefreshResult.Failure
        }

        val refreshBodyJson = refreshRequestAdapter.toJson(
            RefreshTokenRequest(refreshToken = savedRefreshToken)
        )
        val refreshRequest = Request.Builder()
            .url(refreshUrl)
            .header(ACCEPT_HEADER, APPLICATION_JSON)
            .post(refreshBodyJson.toRequestBody(APPLICATION_JSON.toMediaType()))
            .build()

        refreshClient.newCall(refreshRequest).execute().use { refreshResponse ->
            if (!refreshResponse.isSuccessful) {
                return TokenRefreshResult.Failure
            }

            val refreshResponseBody = refreshResponse.body?.string()
                ?: return TokenRefreshResult.Failure
            val refreshData = runCatching { refreshResponseAdapter.fromJson(refreshResponseBody)?.data }
                .getOrNull()
                ?: return TokenRefreshResult.Failure
            val newAccessToken = refreshData.accessToken.orEmpty()
            val newRefreshToken = refreshData.refreshToken.orEmpty()

            if (newAccessToken.isBlank() || newRefreshToken.isBlank()) {
                return TokenRefreshResult.Failure
            }

            // 원 API를 재호출하기 전에 두 토큰의 영속화와 메모리 반영을 모두 완료한다.
            // Interceptor API는 동기식이므로 이 전용 OkHttp 작업 스레드에서 완료를 기다린다.
            runBlocking {
                sessionLocalDataSource.updateTokens(newAccessToken, newRefreshToken)
            }

            return TokenRefreshResult.Success(newAccessToken)
        }
    }

    private fun String.isLoginRequiredError(): Boolean =
        runCatching { errorAdapter.fromJson(this)?.code == TOKEN_EXPIRED_CODE }
            .getOrDefault(false)

    private fun Response.bodySnapshot(): String? =
        runCatching { peekBody(TOKEN_ERROR_BODY_PEEK_BYTES).string() }
            .getOrNull()

    private fun Request.accessTokenFromHeader(): String? =
        header(AUTHORIZATION_HEADER)
            ?.removePrefix(BEARER_PREFIX)
            ?.takeIf(String::isNotBlank)

    private fun Request.isTokenRefreshRetry(): Boolean =
        tag(TokenRefreshRetryMarker::class.java) != null

    private fun Response.rebuildWithBody(bodyString: String): Response =
        newBuilder()
            .body(bodyString.toResponseBody(body?.contentType()))
            .build()

    private data class TokenErrorResponse(
        val code: String?,
    )

    private sealed interface TokenRefreshResult {
        data class Success(val accessToken: String) : TokenRefreshResult
        data object Failure : TokenRefreshResult
    }

    private object TokenRefreshRetryMarker

    companion object {
        private const val TOKEN_EXPIRED_CODE = "A0003"
        private const val TOKEN_ERROR_BODY_PEEK_BYTES = 1024L * 1024L
        private const val ACCEPT_HEADER = "Accept"
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val APPLICATION_JSON = "application/json"
        private const val BEARER_PREFIX = "Bearer "
        private val refreshLock = Any()
    }
}
