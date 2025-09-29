package com.kyu.jiu_jitsu.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.domain.usecase.GetRandomUserUseCase
import com.kyu.jiu_jitsu.domain.usecase.GetSnsLoginUseCase
import com.kyu.jiu_jitsu.login.screen.LoginType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getRandomUserUseCase: GetRandomUserUseCase,
    private val getSnsLoginUseCase: GetSnsLoginUseCase,
): ViewModel() {

    fun startSnsLogin(type: LoginType, context: Context) {
        when(type) {
            is LoginType.KAKAO_ACCOUNT -> {
                loginWithKakaoTalk(context)
            }
            is LoginType.GOOGLE -> {

            }
            is LoginType.APPLE -> {

            }
        }
    }

    private fun loginWithKakaoTalk(context: Context) {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KakaoTalk", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i("KakaoTalk", "카카오계정으로 로그인 성공 ${token.accessToken}")
            }
        }

        viewModelScope.launch {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        Log.e("KakaoTalk", "카카오톡으로 로그인 실패", error)
                    } else if (token != null) {
                        Log.i("KakaoTalk", "카카오톡으로 로그인 성공 ${token.accessToken}")
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }
    }

    fun getSnsLoginInfo(snsProvider: String, token: String) {
        viewModelScope.launch {
            getSnsLoginUseCase(snsProvider, token).collectLatest { uiState ->
                when(uiState) {
                    is UiState.Success -> {
                        Log.d("LoginViewModel", "getSnsLoginInfo Success : ${uiState.result}")
                    }
                    is UiState.Error -> {
                        Log.d("LoginViewModel", "getSnsLoginInfo Error : ${uiState.message}")
                    }
                    is UiState.Idle -> {
                        Log.d("LoginViewModel", "getSnsLoginInfo Idle")
                    }
                    is UiState.Loading -> {
                        Log.d("LoginViewModel", "getSnsLoginInfo Loading")
                    }
                }
            }
        }
    }

    fun getUser(results: Int = 20) {
        viewModelScope.launch {
            getRandomUserUseCase(results).collect { uiState ->
                when(uiState) {
                    is UiState.Success -> {
                        Log.d("LoginViewModel", "getUser Success : ${uiState.result}")
                    }
                    is UiState.Error -> {
                        Log.d("LoginViewModel", "getUser Error : ${uiState.message}")
                    }
                    is UiState.Idle -> {
                        Log.d("LoginViewModel", "getUser Idle")
                    }
                    is UiState.Loading -> {
                        Log.d("LoginViewModel", "getUser Loading")
                    }
                }
            }
        }
    }

}