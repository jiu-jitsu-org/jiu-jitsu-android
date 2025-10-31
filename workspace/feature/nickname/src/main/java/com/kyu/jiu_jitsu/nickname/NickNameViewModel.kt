package com.kyu.jiu_jitsu.nickname

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.domain.isValidUserNickName
import com.kyu.jiu_jitsu.domain.usecase.user.GetLocalNickNameUseCase
import com.kyu.jiu_jitsu.domain.usecase.user.SaveLocalUserInfoUseCase
import com.kyu.jiu_jitsu.domain.usecase.user.SignupUseCase
import com.kyu.jiu_jitsu.domain.usecase.user.UpdateUserProfileUseCase
import com.kyu.jiu_jitsu.nickname.screen.NickNameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NickNameViewModel @Inject constructor(
    private val getLocalNickNameUseCase: GetLocalNickNameUseCase,
    private val signupUseCase: SignupUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val saveLocalUserInfoUseCase: SaveLocalUserInfoUseCase,
): ViewModel() {
    /** 로컬에 저장된 닉네임 조회 상태 */
    var localNickNameState by mutableStateOf<UiState<String>>(UiState.Idle)
    /** 입력받은 닉네임의 유효성 검사 상태 (NickNameState) */
    var inputNickNameState by mutableStateOf<NickNameState>(NickNameState.Idle)

    /** Get Local NickName 로컬에 저장된 닉네임 조회 */
    suspend fun getLocalNickName() {
        localNickNameState = UiState.Loading
        getLocalNickNameUseCase().collectLatest { nickName ->
            localNickNameState = UiState.Success(nickName ?: "")
        }
    }

    fun updateUserProfile() {

    }

    fun saveLocalUserInfo() {

    }

    /**
     * Bottom Button Click Action
     * @param inputNickName: String 입력받은 닉네임
     */
    fun onClickNickNameBottomBtn(
        inputNickName: String,
        isMarketingAgreed: Boolean,
    ) {
        viewModelScope.launch {
            inputNickNameState = NickNameState.Loading
            async { validateNickNameRole(inputNickName) }.await()

            if (inputNickNameState is NickNameState.ValidationSuccess) {
                signUp(
                    inputNickName,
                    isMarketingAgreed
                )
            }
        }

    }

    /**
     * Validation NickName Role
     * @param inputNickName: String 입력받은 닉네임
     */
    private fun validateNickNameRole(inputNickName: String) {
        inputNickNameState = if (inputNickName.isValidUserNickName()) {
            NickNameState.ValidationSuccess
        } else {
            NickNameState.ValidationError
        }
    }


    /**
     * Check Duplicate NickName & Sign Up
     */
    private fun signUp(
        nickName: String,
        isMarketingAgreed: Boolean,
    ) {
        viewModelScope.launch {
            signupUseCase(nickName, isMarketingAgreed).collectLatest { uiState ->
                when(uiState) {
                    is UiState.Success -> {
                        with(uiState.result) {
                            saveLocalUserInfoUseCase(
                                token = accessToken,
                                refreshToken = refreshToken,
                                nickName = userInfo?.nickname,
                                userProfileImg = userInfo?.profileImageUrl,
                            )
                        }
                        inputNickNameState = NickNameState.Succeed
                    }
                    is UiState.Error -> {
                        inputNickNameState = NickNameState.Error(uiState.message)
                    }
                    else -> {
                        Log.d("LoginViewModel", "signUp else")
                    }
                }
            }
        }
    }
}