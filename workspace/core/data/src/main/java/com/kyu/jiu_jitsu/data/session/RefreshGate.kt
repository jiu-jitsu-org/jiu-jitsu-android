package com.kyu.jiu_jitsu.data.session

import com.kyu.jiu_jitsu.model.AppError
import com.kyu.jiu_jitsu.model.AppErrorKind
import com.kyu.jiu_jitsu.model.AppResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Shared by REST and every WebView. The store commits only if the account revision still matches. */
internal class RefreshGate(
    private val snapshot: suspend () -> TokenSnapshot,
    private val fetch: suspend (String) -> AppResult<Pair<String, String>>,
    private val commit: suspend (TokenSnapshot, String, String) -> Boolean,
) {
    private val lock = Mutex()
    @Volatile private var completion = 0L
    private var lastFailure: AppResult.Failure? = null
    private var lastRevision = -1L
    private var lastToken: String? = null
    suspend fun refresh(expiredToken: String?, expectedRevision: Long? = null): AppResult<String> {
        val enteredAt = completion
        return lock.withLock {
            val before = snapshot()
            if (expectedRevision != null && before.revision != expectedRevision) return@withLock expired()
            val access = before.accessToken
            if (access.isNullOrBlank()) return@withLock expired()
            if (access != expiredToken) return@withLock AppResult.Success(access)
            if (completion > enteredAt && lastRevision == before.revision && lastToken == access) {
                lastFailure?.let { return@withLock it }
            }
            val refresh = before.refreshToken?.takeIf { it.isNotBlank() } ?: return@withLock expired()
            val outcome = when (val result = fetch(refresh)) {
                is AppResult.Failure -> result
                is AppResult.Success -> {
                    val (newAccess, newRefresh) = result.data
                    if (newAccess.isBlank() || newRefresh.isBlank() || !commit(before, newAccess, newRefresh)) expired()
                    else AppResult.Success(newAccess)
                }
            }
            lastFailure = outcome as? AppResult.Failure
            lastRevision = before.revision
            lastToken = access
            completion++
            outcome
        }
    }
    private fun expired() = AppResult.Failure(AppError(AppErrorKind.SERVER))
}
