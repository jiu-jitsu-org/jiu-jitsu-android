package com.kyu.jiu_jitsu.setting.viewmodel

import com.kyu.jiu_jitsu.data.repository.SessionRepository
import com.kyu.jiu_jitsu.model.AppResult
import com.kyu.jiu_jitsu.model.SessionUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun sessionChangesUpdateVisibleAccountState() = runTest(dispatcher) {
        val sessions = FakeSessions()
        val model = SettingViewModel(sessions)
        assertNull(model.uiState.value.isLoggedIn)
        runCurrent()
        assertEquals(false, model.uiState.value.isLoggedIn)
        sessions.accessToken.value = "test-session"
        runCurrent()
        assertEquals(true, model.uiState.value.isLoggedIn)
        sessions.accessToken.value = null
        runCurrent()
        assertEquals(false, model.uiState.value.isLoggedIn)
    }

    @Test fun logoutClearsSessionAndIgnoresRepeatedClicks() = runTest(dispatcher) {
        val sessions = FakeSessions("test-session")
        val model = SettingViewModel(sessions)
        runCurrent()
        model.logout()
        model.logout()
        assertTrue(model.uiState.value.isLoggingOut)
        runCurrent()
        assertEquals(1, sessions.clearCalls)
        assertEquals(false, model.uiState.value.isLoggedIn)
        assertFalse(model.uiState.value.isLoggingOut)
    }

    @Test fun failedLogoutRetainsSessionAndAllowsRetry() = runTest(dispatcher) {
        val sessions = FakeSessions("test-session")
        sessions.failClear = true
        val model = SettingViewModel(sessions)
        runCurrent()
        model.logout()
        runCurrent()
        assertTrue(model.uiState.value.logoutFailed)
        assertEquals(true, model.uiState.value.isLoggedIn)
        assertFalse(model.uiState.value.isLoggingOut)
        sessions.failClear = false
        model.logout()
        runCurrent()
        assertFalse(model.uiState.value.logoutFailed)
        assertEquals(false, model.uiState.value.isLoggedIn)
    }

    @Test fun guestCannotInvokeLogout() = runTest(dispatcher) {
        val sessions = FakeSessions()
        val model = SettingViewModel(sessions)
        runCurrent()
        model.logout()
        runCurrent()
        assertEquals(0, sessions.clearCalls)
    }

    private class FakeSessions(token: String? = null) : SessionRepository {
        override val accessToken = MutableStateFlow(token)
        override val revision = MutableStateFlow(0L)
        override val nickname = MutableStateFlow<String?>(null)
        override val profileImageUrl = MutableStateFlow<String?>(null)
        var failClear = false
        var clearCalls = 0
        override suspend fun isLoggedIn() = !accessToken.value.isNullOrBlank()
        override suspend fun clearSession() {
            clearCalls++
            if (failClear) error("Test persistence failure")
            accessToken.value = null
        }
        override suspend fun refreshAccessToken(expiredAccessToken: String?): AppResult<String> = error("Unused")
        override suspend fun updateSession(update: SessionUpdate) = Unit
        override suspend fun updateCachedProfile(nickname: String?, profileImageUrl: String?) = Unit
        override fun setTransientAccessToken(accessToken: String?) = Unit
    }
}
