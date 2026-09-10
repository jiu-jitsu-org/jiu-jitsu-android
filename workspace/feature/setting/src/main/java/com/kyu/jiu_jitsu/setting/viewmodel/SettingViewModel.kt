package com.kyu.jiu_jitsu.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyu.jiu_jitsu.data.repository.SessionRepository
import com.kyu.jiu_jitsu.setting.model.SettingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
internal class SettingViewModel @Inject constructor(
    private val sessions: SessionRepository,
) : ViewModel() {
    private val mutableState = MutableStateFlow(SettingUiState())
    val uiState = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            sessions.accessToken.collect { token ->
                mutableState.update { it.copy(isLoggedIn = !token.isNullOrBlank()) }
            }
        }
    }

    fun logout() {
        if (uiState.value.isLoggedIn != true || uiState.value.isLoggingOut) return
        mutableState.update { it.copy(isLoggingOut = true, logoutFailed = false) }
        viewModelScope.launch {
            try {
                // Use the same local session invalidation as the existing web logout flow.
                sessions.clearSession()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.update { it.copy(logoutFailed = true) }
            } finally {
                mutableState.update { it.copy(isLoggingOut = false) }
            }
        }
    }
}
