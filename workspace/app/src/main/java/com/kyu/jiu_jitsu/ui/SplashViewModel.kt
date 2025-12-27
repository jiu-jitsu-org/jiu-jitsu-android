package com.kyu.jiu_jitsu.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.model.SplashModel
import com.kyu.jiu_jitsu.data.model.UserProfileInfo
import com.kyu.jiu_jitsu.domain.usecase.GetBootStrapInfoUseCase
import com.kyu.jiu_jitsu.domain.usecase.community.GetCommunityProfileUseCase
import com.kyu.jiu_jitsu.domain.usecase.login.CheckAutoLoginUseCase
import com.kyu.jiu_jitsu.domain.usecase.user.GetUserProfileUseCase
import com.kyu.jiu_jitsu.domain.usecase.user.SaveLocalUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getBootStrapInfoUseCase: GetBootStrapInfoUseCase,
    private val checkAutoLoginUseCase: CheckAutoLoginUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val saveLocalUserInfoUseCase: SaveLocalUserInfoUseCase,
    private val getCommunityProfile: GetCommunityProfileUseCase,
) : ViewModel() {

    var splashUiState by mutableStateOf<UiState<SplashModel>>(UiState.Idle)
    var autoLoginState by mutableStateOf<UiState<UserProfileInfo?>>(UiState.Idle)

    // BootStrap + Check Auto Login (Check Local Token)
    suspend fun startFirstLogic() {
        splashUiState = UiState.Loading
        combine(
            getBootStrapInfoUseCase(),
            checkAutoLoginUseCase()
        ) { bootState, autoLogin ->
            when (bootState) {
                is UiState.Success -> UiState.Success(
                    SplashModel(bootState.result, autoLogin)
                )

                is UiState.Error -> bootState
                else -> UiState.Idle
            }
        }.collectLatest { newState ->
            splashUiState = newState
        }
    }

    /**
     * Fetch User Profile to token
     */
    suspend fun tryAutoLogin() {
        // TODO chan 첫 진입에서 모든 로그인 관련 데이터를 다 받아와야 좋을까? - GetCommunityProfileUseCase
        autoLoginState = UiState.Loading
        getUserProfileUseCase().collectLatest { uiState ->
            when(uiState) {
                is UiState.Success -> {
                    with(uiState.result) {
                        saveLocalUserInfoUseCase(
                            nickName = nickname,
                            userProfileImg = profileImageUrl,
                        )
                    }
                    autoLoginState = UiState.Success(uiState.result)
                }
                is UiState.Error -> {
                    // TODO chan refresh token
                    if (uiState.code != null) {
                        when(uiState.code) {
                            500 -> autoLoginState = UiState.Error(code = uiState.code, message = uiState.message, retryable =  false)
                            else -> autoLoginState = UiState.Error(message = uiState.message, retryable =  false)
                        }
                    }

                }
                else -> {}
            }
        }
    }

    // Open GooglePlay Store for App Update
    fun openGooglePlayStore() {
        // TODO Open GooglePlay Store
    }

    // Compare App Version for App Update
    fun isNeedAppUpdate(): Boolean {

        return false
    }

}