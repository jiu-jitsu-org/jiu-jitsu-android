package com.kyu.jiu_jitsu.web

import androidx.lifecycle.ViewModel
import com.kyu.jiu_jitsu.data.repository.SessionRepository
import com.kyu.jiu_jitsu.data.repository.WebSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class WebViewModel @Inject constructor(
    private val sessions: SessionRepository,
    private val webSessions: WebSessionRepository,
) : ViewModel() {
    val origin get() = webSessions.origin
    val debuggingEnabled get() = webSessions.debuggingEnabled
    val revisions get() = sessions.revision
    suspend fun revision() = sessions.revision.first()
    suspend fun token() = sessions.accessToken.first()
    suspend fun prepare() = webSessions.prepare()
    suspend fun refresh(token: String?) = sessions.refreshAccessToken(token)
    suspend fun logout() = sessions.clearSession()
}
