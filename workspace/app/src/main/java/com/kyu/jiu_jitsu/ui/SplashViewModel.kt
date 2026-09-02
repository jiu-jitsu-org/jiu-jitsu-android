package com.kyu.jiu_jitsu.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.kyu.jiu_jitsu.data.repository.BootStrapRepository
import com.kyu.jiu_jitsu.data.repository.SessionRepository
import com.kyu.jiu_jitsu.data.repository.UserRepository
import com.kyu.jiu_jitsu.model.AppResult
import com.kyu.jiu_jitsu.model.SplashModel
import com.kyu.jiu_jitsu.model.UserProfileInfo
import com.kyu.jiu_jitsu.ui.state.UiState
import com.kyu.jiu_jitsu.ui.state.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** Coordinates startup policy and authenticated-session verification for the splash route. */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val bootStrapRepository: BootStrapRepository,
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    var splashUiState by mutableStateOf<UiState<SplashModel>>(UiState.Idle)
        private set

    var autoLoginState by mutableStateOf<UiState<UserProfileInfo?>>(UiState.Idle)
        private set

    /**
     * Loads the server's startup policy and hydrates the in-memory request token from DataStore.
     *
     * Both values are required for the next navigation decision. A fabricated bootstrap value
     * would silently bypass force-update policy, so a backend failure is surfaced to the screen.
     */
    suspend fun startFirstLogic() {
        splashUiState = UiState.Loading
        when (val result = bootStrapRepository.getBootStrapInfo()) {
            is AppResult.Success -> {
                splashUiState = UiState.Success(
                    SplashModel(
                        bootStrapInfo = result.data,
                        autoLogin = sessionRepository.isLoggedIn(),
                    ),
                )
            }

            is AppResult.Failure -> splashUiState = result.error.toUiError()
        }
    }

    /** Verifies that the persisted access token still resolves to an active user. */
    suspend fun tryAutoLogin() {
        autoLoginState = UiState.Loading
        when (val result = userRepository.getUserProfileInfo()) {
            is AppResult.Success -> {
                val profile = result.data
                // Keep lightweight display data local without rewriting the existing token pair.
                sessionRepository.updateCachedProfile(
                    nickname = profile.nickname,
                    profileImageUrl = profile.profileImageUrl,
                )
                autoLoginState = UiState.Success(profile)
            }

            is AppResult.Failure -> {
                val error = result.error
                // Only an explicit authentication failure invalidates the local session. Network,
                // server, and parsing failures remain retryable and must not silently log users out.
                if (
                    error.httpCode == 401 ||
                    error.serverCode == LOGIN_REQUIRED_CODE ||
                    error.serverCode == USER_NOT_FOUND_CODE
                ) {
                    sessionRepository.clearSession()
                }
                autoLoginState = error.toUiError()
            }
        }
    }

    /** Platform-specific store navigation remains a deliberate app-layer responsibility. */
    fun openGooglePlayStore() {
        // TODO: Route an intent through the screen so this ViewModel remains Context-free.
    }

    /** Version comparison will use the bootstrap policy once the store-update flow is implemented. */
    fun isNeedAppUpdate(): Boolean = false

    private companion object {
        private const val LOGIN_REQUIRED_CODE = "A0003"
        private const val USER_NOT_FOUND_CODE = "U0002"
    }
}
