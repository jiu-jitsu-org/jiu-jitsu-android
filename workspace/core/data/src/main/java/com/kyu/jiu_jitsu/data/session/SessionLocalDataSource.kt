package com.kyu.jiu_jitsu.data.session

import com.kyu.jiu_jitsu.data.datastore.PrefKeys
import com.kyu.jiu_jitsu.data.datastore.SecurePreferences
import com.kyu.jiu_jitsu.model.SessionUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Coordinates encrypted session persistence with the synchronous token used by networking.
 *
 * This class is public only so Hilt-generated code can construct it across compilation units; it
 * is a data-layer implementation detail and must not be imported by app, domain, or feature code.
 */
@Singleton
class SessionLocalDataSource @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val accessTokenProvider: AccessTokenProvider,
) {
    private val mutationLock = Mutex()
    val revision = MutableStateFlow(0L)

    val accessToken: Flow<String?> =
        securePreferences.getValueToDecrypt(PrefKeys.USER_TOKEN)

    val nickname: Flow<String?> =
        securePreferences.getValueToDecrypt(PrefKeys.USER_NICK_NAME)

    val profileImageUrl: Flow<String?> =
        securePreferences.getValueToDecrypt(PrefKeys.USER_PROFILE_IMG)

    suspend fun isLoggedIn(): Boolean = !tokenSnapshot().accessToken.isNullOrBlank()

    suspend fun updateSession(update: SessionUpdate) = mutationLock.withLock {
        // Persist the complete session in one DataStore transaction before publishing it to
        // OkHttp. A request can therefore never use a token pair that was only partly written.
        securePreferences.setEncryptedValues(
            mapOf(
                Pair(PrefKeys.USER_TOKEN, update.accessToken),
                Pair(PrefKeys.USER_REFRESH_TOKEN, update.refreshToken),
                Pair(PrefKeys.USER_NICK_NAME, update.nickname),
                Pair(PrefKeys.USER_PROFILE_IMG, update.profileImageUrl),
            ),
        )
        accessTokenProvider.update(update.accessToken, revision.value + 1)
        revision.value++
    }

    suspend fun tokenSnapshot(): TokenSnapshot = mutationLock.withLock {
        val token = accessToken.first()
        accessTokenProvider.update(token, revision.value)
        TokenSnapshot(revision.value, token, refreshToken())
    }

    suspend fun updateTokensIfCurrent(snapshot: TokenSnapshot, accessToken: String, refreshToken: String): Boolean =
        mutationLock.withLock {
            if (revision.value != snapshot.revision || this.accessToken.first() != snapshot.accessToken) return@withLock false
            securePreferences.setEncryptedValues(mapOf(
                Pair(PrefKeys.USER_TOKEN, accessToken),
                Pair(PrefKeys.USER_REFRESH_TOKEN, refreshToken),
            ))
            accessTokenProvider.update(accessToken, revision.value)
            true
        }

    suspend fun updateCachedProfile(nickname: String?, profileImageUrl: String?) {
        securePreferences.setEncryptedValues(
            mapOf(
                Pair(PrefKeys.USER_NICK_NAME, nickname),
                Pair(PrefKeys.USER_PROFILE_IMG, profileImageUrl),
            ),
        )
    }

    suspend fun refreshToken(): String? =
        securePreferences.getValueToDecrypt(PrefKeys.USER_REFRESH_TOKEN).first()

    fun currentAccessToken(): String? = accessTokenProvider.current()

    fun setTransientAccessToken(accessToken: String?) {
        // Sign-up APIs require the short-lived login token, but it must not be persisted as a
        // durable authenticated session before sign-up succeeds.
        accessTokenProvider.update(accessToken, revision.value)
    }

    suspend fun clear() = mutationLock.withLock {
        securePreferences.clear()
        accessTokenProvider.update(null, revision.value + 1)
        revision.value++
    }
}

/** Sensitive snapshot: intentionally has no generated toString(). */
class TokenSnapshot(val revision: Long, val accessToken: String?, val refreshToken: String?)
