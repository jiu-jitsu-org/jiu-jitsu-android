package com.kyu.jiu_jitsu.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kyu.jiu_jitsu.data.repository.SessionRepository
import com.kyu.jiu_jitsu.data.repository.SnsLoginRepository
import com.kyu.jiu_jitsu.login.model.LoginType
import com.kyu.jiu_jitsu.login.model.SnsLoginSucceedType
import com.kyu.jiu_jitsu.model.AppResult
import com.kyu.jiu_jitsu.model.SessionUpdate
import com.kyu.jiu_jitsu.ui.state.UiState
import com.kyu.jiu_jitsu.ui.state.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Owns provider sign-in and converts its credential into an application session. */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val snsLoginRepository: SnsLoginRepository,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    var loginType by mutableStateOf<LoginType>(LoginType.GOOGLE)

    // Provider credentials are scoped to this ViewModel instance. They are neither saved in
    // Compose state nor attached to singleton provider objects, which limits accidental exposure.
    private var providerToken: String? = null

    var loginUiState by mutableStateOf<UiState<SnsLoginSucceedType>>(UiState.Idle)
        private set

    /** Development helper retained for the existing test server flow. */
    fun startKakaoTestLogin() {
        if (!BuildConfig.DEV) return
        loginType = LoginType.KAKAO_ACCOUNT
        providerToken = "kakaoTest"
        getSnsLoginInfo()
    }

    /** Starts the SDK flow selected by [loginType]. */
    fun startSnsLogin(context: Context) {
        when (loginType) {
            LoginType.KAKAO_ACCOUNT -> loginWithKakaoTalk(context)
            LoginType.GOOGLE -> signInWithGoogle(context)
            LoginType.APPLE -> {
                // TODO: Add the Android web-based Apple authorization flow.
            }
        }
    }

    private fun buildGoogleIdOption(
        webClientId: String,
        nonce: String? = null,
    ): GetGoogleIdOption = GetGoogleIdOption.Builder()
        // The backend validates a token minted for the web OAuth client, not the Android client.
        .setServerClientId(webClientId)
        .setFilterByAuthorizedAccounts(false)
        .setAutoSelectEnabled(true)
        .apply { nonce?.let(::setNonce) }
        .build()

    fun signInWithGoogle(
        context: Context,
        onSuccess: (GoogleIdTokenCredential) -> Unit = {},
        onCancelOrError: (Throwable?) -> Unit = {},
    ) {
        val credentialManager = CredentialManager.create(context)
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(buildGoogleIdOption(BuildConfig.GOOGLE_OAUTH_WEB_CLIENT_ID))
            .build()

        viewModelScope.launch {
            try {
                val credential = credentialManager.getCredential(context, request).credential
                if (
                    credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    loginType = LoginType.GOOGLE
                    providerToken = googleCredential.idToken
                    onSuccess(googleCredential)
                    getSnsLoginInfo()
                } else {
                    onCancelOrError(IllegalStateException("Unsupported Google credential type."))
                }
            } catch (error: GetCredentialException) {
                // Never log an ID token or provider access token. The exception category is enough
                // for diagnostics and avoids leaking credentials through Logcat or crash reports.
                Log.w(LOG_TAG, "Google credential request did not complete.", error)
                onCancelOrError(error)
            }
        }
    }

    private fun loginWithKakaoTalk(context: Context) {
        val accountCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            handleKakaoResult(token, error)
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context, callback = ::handleKakaoResult)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = accountCallback)
        }
    }

    private fun handleKakaoResult(token: OAuthToken?, error: Throwable?) {
        if (error != null) {
            Log.w(LOG_TAG, "Kakao credential request did not complete.", error)
            return
        }
        if (token != null) {
            loginType = LoginType.KAKAO_ACCOUNT
            providerToken = token.accessToken
            getSnsLoginInfo()
        }
    }

    /** Exchanges the provider credential for either a temporary sign-up token or a full session. */
    fun getSnsLoginInfo() {
        viewModelScope.launch {
            loginUiState = UiState.Loading
            // Copy then clear the provider credential before suspension so it cannot remain in
            // memory for the lifetime of this ViewModel after the exchange completes.
            val credential = providerToken.orEmpty()
            providerToken = null
            when (
                val result = snsLoginRepository.login(
                    snsProvider = loginType.type,
                    token = credential,
                )
            ) {
                is AppResult.Success -> {
                    val loginInfo = result.data
                    if (loginInfo.isNewUser) {
                        // The temporary token authorizes sign-up only and intentionally remains
                        // memory-only until the backend returns a durable session.
                        sessionRepository.setTransientAccessToken(loginInfo.tempToken)
                        loginUiState = UiState.Success(SnsLoginSucceedType.SIGN_UP)
                    } else {
                        sessionRepository.updateSession(
                            SessionUpdate(
                                accessToken = loginInfo.accessToken,
                                refreshToken = loginInfo.refreshToken,
                                nickname = loginInfo.userInfo?.nickname,
                                profileImageUrl = loginInfo.userInfo?.profileImageUrl,
                            ),
                        )
                        loginUiState = UiState.Success(SnsLoginSucceedType.SIGN_IN)
                    }
                }

                is AppResult.Failure -> loginUiState = result.error.toUiError()
            }
        }
    }

    private companion object {
        private const val LOG_TAG = "LoginViewModel"
    }
}
