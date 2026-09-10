package kr.bjj_oss.data.api.interceptor

import kr.bjj_oss.data.session.TokenRefreshCoordinator
import kr.bjj_oss.data.session.SessionRequestRevision
import kr.bjj_oss.model.AppResult
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/** Preserves the REST A0003/one-retry contract while sharing rotation with all WebViews. */
class TokenRefreshInterceptor(
    private val coordinator: TokenRefreshCoordinator,
    moshi: Moshi,
) : Interceptor {
    private val errorAdapter = moshi.adapter(TokenErrorResponse::class.java)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val revision = request.tag(SessionRequestRevision::class.java)?.value ?: coordinator.revision
        val response = chain.proceed(request)
        val expired = runCatching {
            errorAdapter.fromJson(response.peekBody(1024L * 1024L).string())?.code == "A0003"
        }.getOrDefault(false)
        if (!expired) return response
        val token = request.header("Authorization")?.removePrefix("Bearer ")
        val result = runBlocking { coordinator.refresh(token, revision) }
        if (result !is AppResult.Success) return response
        response.close()
        // Calling proceed here continues down the chain; it cannot re-enter this interceptor.
        return chain.proceed(request.newBuilder().header("Authorization", "Bearer ${result.data}").build())
    }
    private data class TokenErrorResponse(val code: String?)
}
