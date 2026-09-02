package com.kyu.jiu_jitsu.data.session

import com.kyu.jiu_jitsu.data.datastore.PrefKeys
import com.kyu.jiu_jitsu.data.datastore.SecurePreferences
import com.kyu.jiu_jitsu.model.SessionUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
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
    val accessToken: Flow<String?> =
        securePreferences.getValueToDecrypt(PrefKeys.USER_TOKEN)
            .onEach(accessTokenProvider::update)

    val nickname: Flow<String?> =
        securePreferences.getValueToDecrypt(PrefKeys.USER_NICK_NAME)

    val profileImageUrl: Flow<String?> =
        securePreferences.getValueToDecrypt(PrefKeys.USER_PROFILE_IMG)

    suspend fun isLoggedIn(): Boolean {
        val storedToken = accessToken.first()
        // Reading the durable source also hydrates the fast request-header view at app startup.
        accessTokenProvider.update(storedToken)
        return !storedToken.isNullOrBlank()
    }

    suspend fun updateSession(update: SessionUpdate) {
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
        accessTokenProvider.update(update.accessToken)
    }

    suspend fun updateTokens(accessToken: String, refreshToken: String) {
        securePreferences.setEncryptedValues(
            mapOf(
                Pair(PrefKeys.USER_TOKEN, accessToken),
                Pair(PrefKeys.USER_REFRESH_TOKEN, refreshToken),
            ),
        )
        accessTokenProvider.update(accessToken)
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
        accessTokenProvider.update(accessToken)
    }

    suspend fun clear() {
        securePreferences.clear()
        accessTokenProvider.update(null)
    }
}
