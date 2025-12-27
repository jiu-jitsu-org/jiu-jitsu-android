package com.kyu.jiu_jitsu.nickname

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.domain.isValidUserNickName
import com.kyu.jiu_jitsu.domain.usecase.user.CheckNickNameUseCase
import com.kyu.jiu_jitsu.domain.usecase.user.GetLocalNickNameUseCase
import com.kyu.jiu_jitsu.domain.usecase.user.SaveLocalUserInfoUseCase
import com.kyu.jiu_jitsu.domain.usecase.user.SignupUseCase
import com.kyu.jiu_jitsu.domain.usecase.user.UpdateUserProfileUseCase
import com.kyu.jiu_jitsu.nickname.model.NickNameState
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
    private val checkNickNameUseCase: CheckNickNameUseCase,
) : ViewModel() {
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
    fun onClickValidateNickname(
        inputNickName: String,
    ) {
        viewModelScope.launch {
            inputNickNameState = NickNameState.Loading

            // 닉네임 유효성 체크 시작
            async { validateNickNameRole(inputNickName) }.await()
            if (inputNickNameState is NickNameState.ValidationSuccess)
                async { checkNickname(inputNickName) }.await()
        }
    }

    /**
     * Bottom Button Click Action
     * @param inputNickName: String 입력받은 닉네임
     * @param isMarketingAgreed: Boolean 마케팅 동의 여부
     */
    fun onClickSignUp(
        inputNickName: String,
        isMarketingAgreed: Boolean,
    ) {
        inputNickNameState = NickNameState.Loading

        viewModelScope.launch {
            // 닉네임 체크 완료 이후 회원가입
            signUp(
                inputNickName,
                isMarketingAgreed
            )
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
     * Sign Up
     */
    private fun signUp(
        nickName: String,
        isMarketingAgreed: Boolean,
    ) {
        viewModelScope.launch {
            signupUseCase(nickName, isMarketingAgreed).collectLatest { uiState ->
                when (uiState) {
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

    /**
     * Check Duplicate NickName
     * @param nickName: String 입력받은 닉네임
     */
    private fun checkNickname(
        nickName: String,
    ) {
        viewModelScope.launch {
            checkNickNameUseCase(nickName).collectLatest { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        inputNickNameState = NickNameState.ValidationSuccess
                    }

                    is UiState.Error -> {
                        inputNickNameState =
                            if (uiState.code != null) {
                                when (uiState.code) {
                                    400 -> NickNameState.DuplicateError
                                    else -> NickNameState.Error(uiState.message)
                                }
                            } else {
                                NickNameState.Error(uiState.message)
                            }
                    }

                    else -> {
                        Log.d("LoginViewModel", "checkNickname else")
                    }
                }
            }
        }
    }
}