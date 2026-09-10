package kr.bjj_oss.data.repository.impl

import android.util.Base64
import android.webkit.CookieManager
import kr.bjj_oss.data.BuildConfig
import kr.bjj_oss.data.repository.WebSessionRepository
import kr.bjj_oss.data.session.SessionLocalDataSource
import kr.bjj_oss.data.session.TokenRefreshCoordinator
import kr.bjj_oss.model.AppError
import kr.bjj_oss.model.AppErrorKind
import kr.bjj_oss.model.AppResult
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume

internal class WebSessionRepositoryImpl @Inject constructor(
    private val store: SessionLocalDataSource,
    private val refresh: TokenRefreshCoordinator,
    moshi: Moshi,
) : WebSessionRepository {
    override val origin = BuildConfig.WEB_ORIGIN
    override val debuggingEnabled = BuildConfig.DEBUG
    private val lock = Mutex()
    private val client = OkHttpClient.Builder().followRedirects(false).followSslRedirects(false)
        .retryOnConnectionFailure(false).callTimeout(8, TimeUnit.SECONDS).build()
    private val adapter = moshi.adapter(Map::class.java)

    override suspend fun prepare(): AppResult<String?> = lock.withLock {
        try {
            val url = origin.toHttpUrl()
            require(url.username.isEmpty() && url.password.isEmpty() && url.encodedPath == "/" && url.query == null && url.fragment == null)
            require(url.isHttps || (debuggingEnabled && url.host in setOf("localhost", "127.0.0.1", "10.0.2.2")))
            var snapshot = store.tokenSnapshot()
            if (snapshot.accessToken?.let(::knownExpired) == true) {
                val result = refresh.refresh(snapshot.accessToken)
                if (result is AppResult.Failure) return@withLock result
                snapshot = store.tokenSnapshot()
            }
            val access = snapshot.accessToken
            val cookies = if (access.isNullOrBlank()) {
                listOf("oss_session=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax" + if (url.isHttps) "; Secure" else "")
            } else withContext(Dispatchers.IO) {
                val request = Request.Builder().url(url.newBuilder().encodedPath("/api/auth/session").build())
                    .post(adapter.toJson(mapOf("accessToken" to access)).toRequestBody("application/json".toMediaType())).build()
                client.newCall(request).execute().use { response ->
                    val body = response.body?.string()?.let(adapter::fromJson)
                    require(response.code == 200 && body?.get("success") == true &&
                        (body["data"] as? Map<*, *>)?.get("authenticated") == true)
                    response.headers("Set-Cookie").also { headers ->
                        require(headers.isNotEmpty())
                        require(headers.all { header ->
                            Cookie.parse(url, header)?.let { cookie ->
                                cookie.name == "oss_session" && cookie.hostOnly && cookie.domain == url.host &&
                                    cookie.path == "/" && cookie.httpOnly && (!url.isHttps || cookie.secure)
                            } == true
                        })
                    }
                }
            }
            if (store.revision.value != snapshot.revision) return@withLock failure()
            // Finish callbacks even if the old screen leaves; the next preparation is serialized.
            withContext(NonCancellable + Dispatchers.Main) {
                val manager = CookieManager.getInstance()
                manager.setAcceptCookie(true)
                for (cookie in cookies) {
                    val accepted = suspendCancellableCoroutine { continuation ->
                        manager.setCookie(origin, cookie) { continuation.resume(it) }
                    }
                    check(accepted)
                }
                manager.flush()
            }
            if (store.revision.value != snapshot.revision) failure() else AppResult.Success(access)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: com.squareup.moshi.JsonDataException) {
            AppResult.Failure(AppError(AppErrorKind.SERIALIZATION))
        } catch (_: IOException) {
            AppResult.Failure(AppError(AppErrorKind.NETWORK, retryable = true))
        } catch (_: IllegalArgumentException) {
            failure()
        } catch (_: IllegalStateException) {
            failure()
        }
    }

    private fun knownExpired(token: String): Boolean = runCatching {
        val payload = String(Base64.decode(token.split('.')[1], Base64.URL_SAFE or Base64.NO_WRAP), Charsets.UTF_8)
        val expiry = adapter.fromJson(payload)?.get("exp") as? Number ?: return false
        expiry.toLong() <= System.currentTimeMillis() / 1000 + 30
    }.getOrDefault(false) // Opaque tokens are checked by protected backend calls, never by BFF GET.

    private fun failure() = AppResult.Failure(AppError(AppErrorKind.SERVER, retryable = true))
}
