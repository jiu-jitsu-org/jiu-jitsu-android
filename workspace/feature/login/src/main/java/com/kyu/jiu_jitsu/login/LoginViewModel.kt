package com.kyu.jiu_jitsu.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.model.dto.response.SnsLoginResponse
import com.kyu.jiu_jitsu.data.module.NetworkModule.setUserToken
import com.kyu.jiu_jitsu.data.utils.NetworkConfig
import com.kyu.jiu_jitsu.domain.usecase.login.GetSnsLoginUseCase
import com.kyu.jiu_jitsu.domain.usecase.user.SaveLocalUserInfoUseCase
import com.kyu.jiu_jitsu.domain.usecase.user.SignupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginType(
    val type: String,
    var snsLoginToken: String? = null,
) {
    data object KAKAO_ACCOUNT : LoginType("KAKAO")
    data object GOOGLE : LoginType("GOOGLE")
    data object APPLE : LoginType("APPLE")
}

sealed interface SnsLoginSucceedType{
    data object SIGN_IN: SnsLoginSucceedType
    data object SIGN_UP: SnsLoginSucceedType
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getSnsLoginUseCase: GetSnsLoginUseCase,
    private val signupUseCase: SignupUseCase,
    private val saveLocalUserInfoUseCase: SaveLocalUserInfoUseCase,
): ViewModel() {

    var loginType by mutableStateOf<LoginType>(LoginType.GOOGLE)
    var loginUiState by mutableStateOf<UiState<SnsLoginSucceedType>>(UiState.Idle)

    /**
     * SNS Login
     */
    fun startSnsLogin(
        context: Context,
    ) {
        when(loginType) {
            is LoginType.KAKAO_ACCOUNT -> {
                loginWithKakaoTalk(context)
            }
            is LoginType.GOOGLE -> {
                signInWithGoogle(context)
            }
            is LoginType.APPLE -> {

            }
        }
    }

    private fun buildGoogleIdOption(
        webClientId: String,
        nonce: String? = null,
    ): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            // 기존에 이 앱에 로그인한 적 있는 계정을 우선 필터링
            .setFilterByAuthorizedAccounts(false)
            // 서버 검증 시 서버(Web) 클라이언트 ID 사용
            .setServerClientId(webClientId)
            // 단일 자격이면 자동 로그인 UX 허용(권장)
            .setAutoSelectEnabled(true)
            .apply { nonce?.let { setNonce(it) } }
            .build()
    }

    fun signInWithGoogle(
        context: Context,
        onSuccess: (GoogleIdTokenCredential) -> Unit = {},
        onCancelOrError: (Throwable?) -> Unit = {},
    ) {
        val cm = CredentialManager.create(context)
        val googleId = buildGoogleIdOption(BuildConfig.GOOGLE_OAUTH_WEB_CLIENT_ID)
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleId)
            .build()

        viewModelScope.launch {
            try {
                val result = cm.getCredential(
                    context = context,
                    request = request
                )
                when (val cred = result.credential) {
                    is androidx.credentials.CustomCredential -> {
                        if (cred.type ==
                            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val idCred = GoogleIdTokenCredential.createFrom(cred.data)
                            loginType = LoginType.GOOGLE.apply { snsLoginToken = idCred.idToken }
                            onSuccess(idCred)
                            getSnsLoginInfo()
                        } else {
                            onCancelOrError(IllegalStateException("Unknown credential type"))
                        }
                    }
                    else -> {
                        onCancelOrError(IllegalStateException("Unexpected credential"))
                    }
                }
            } catch (e: GetCredentialException) {
                // 사용자가 취소했거나 네트워크/구성 오류 등
                Log.e("@@@@@@@@", "Google Login Error : ${e.message}")
                onCancelOrError(e)
            }
        }
    }

    private fun loginWithKakaoTalk(context: Context) {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KakaoTalk", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                loginType = LoginType.KAKAO_ACCOUNT.apply { snsLoginToken = token.accessToken }
                Log.i("KakaoTalk", "카카오계정으로 로그인 성공 ${token.accessToken}")
                getSnsLoginInfo()
            }
        }

        viewModelScope.launch {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        Log.e("KakaoTalk", "카카오톡으로 로그인 실패", error)
                    } else if (token != null) {
                        loginType = LoginType.KAKAO_ACCOUNT.apply { snsLoginToken = token.accessToken }
                        Log.i("KakaoTalk", "카카오톡으로 로그인 성공 ${token.accessToken}")
                        getSnsLoginInfo()
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }
    }

    fun getSnsLoginInfo() {
        viewModelScope.launch {
            loginUiState = UiState.Loading
            getSnsLoginUseCase(loginType.type, loginType.snsLoginToken?:"").collectLatest { uiState ->
                when(uiState) {
                    is UiState.Success -> {
                        Log.d("LoginViewModel", "getSnsLoginInfo Success : ${uiState.result}")
                        if (uiState.result.isNewUser) {
                            // New User Need Sign up
                            uiState.result.tempToken.setUserToken()
                            loginUiState = UiState.Success(SnsLoginSucceedType.SIGN_UP)
                        } else {
                            // Sign in
                            with(uiState.result) {
                                saveLocalUserInfoUseCase(
                                    token = accessToken,
                                    refreshToken = refreshToken,
                                    nickName = userInfo?.nickname,
                                    userProfileImg = userInfo?.profileImageUrl,
                                )
                            }
                            loginUiState = UiState.Success(SnsLoginSucceedType.SIGN_IN)
                        }
                    }
                    is UiState.Error -> {
                        Log.d("LoginViewModel", "getSnsLoginInfo Error : ${uiState.message}")
                        loginUiState = UiState.Error(uiState.message, false)
                    }
                    else -> {
                        Log.d("LoginViewModel", "getSnsLoginInfo else")
                    }
                }
            }
        }
    }

}
