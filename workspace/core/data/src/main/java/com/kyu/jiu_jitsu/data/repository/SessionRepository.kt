package com.kyu.jiu_jitsu.data.repository

import com.kyu.jiu_jitsu.model.SessionUpdate
import kotlinx.coroutines.flow.Flow

/**
 * Owns the authenticated session shared by startup, login, and profile features.
 *
 * Features interact with this contract instead of DataStore keys or networking globals. That
 * keeps encrypted persistence and request-header synchronization inside `core:data`.
 */
interface SessionRepository {
    val accessToken: Flow<String?>
    val nickname: Flow<String?>
    val profileImageUrl: Flow<String?>

    suspend fun isLoggedIn(): Boolean
    suspend fun updateSession(update: SessionUpdate)
    suspend fun updateCachedProfile(nickname: String?, profileImageUrl: String?)
    fun setTransientAccessToken(accessToken: String?)
    suspend fun clearSession()
}
