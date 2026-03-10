package com.kyu.jiu_jitsu.nickname

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.domain.isValidUserNickName
import com.kyu.jiu_jitsu.domain.usecase.user.CheckNickNameUseCase
import com.kyu.jiu_jitsu.domain.usecase.local.GetLocalNickNameUseCase
import com.kyu.jiu_jitsu.domain.usecase.local.SaveLocalUserInfoUseCase
import com.kyu.jiu_jitsu.domain.usecase.user.SignupUseCase
import com.kyu.jiu_jitsu.domain.usecase.user.UpdateUserProfileUseCase
import com.kyu.jiu_jitsu.nickname.model.NickNameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface NickNameAction {
    data class SignUp(val inputNickName: String, val isMarketingAgreed: Boolean): NickNameAction
    data class ValidateNickName(val inputNickName: String): NickNameAction
    data object InitNickNameState: NickNameAction
}

@HiltViewModel
class NickNameViewModel @Inject constructor(
    private val getLocalNickNameUseCase: GetLocalNickNameUseCase,
    private val signupUseCase: SignupUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val saveLocalUserInfoUseCase: SaveLocalUserInfoUseCase,
    private val checkNickNameUseCase: CheckNickNameUseCase,
) : ViewModel() {
    /** 로컬에 저장된 닉네임 조회 상태 */
    var _localNickNameState = MutableStateFlow<String?>(null)
    val localNickNameState = _localNickNameState.asStateFlow()


    /** 입력받은 닉네임의 유효성 검사 상태 (NickNameState) */
    val _validateNickNameState = MutableStateFlow<NickNameState>(NickNameState.Idle)
    var validateNickNameState = _validateNickNameState.asStateFlow()

    private var _errorUiState = MutableStateFlow<String?>(null)
    var errorUiState = _errorUiState.asStateFlow()

    private var _loadingUiState = MutableStateFlow(false)
    var loadingUiState = _loadingUiState.asStateFlow()

    init {
        getLocalNickName()
    }

    fun onAction(action: NickNameAction) {
        when(action) {
            is NickNameAction.SignUp -> onClickSignUp(action.inputNickName, action.isMarketingAgreed)
            is NickNameAction.ValidateNickName -> onClickValidateNickname(action.inputNickName)
            NickNameAction.InitNickNameState -> _validateNickNameState.value = NickNameState.Idle
        }
    }

    /** Get Local NickName 로컬에 저장된 닉네임 조회 */
    fun getLocalNickName() {
        viewModelScope.launch {
            getLocalNickNameUseCase().onStart {
                _loadingUiState.value = true
            }.collectLatest { nickName ->
                _loadingUiState.value = false
                _localNickNameState.value = nickName
            }
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
    private fun onClickValidateNickname(
        inputNickName: String,
    ) {
        // 닉네임 유효성 체크 시작
        if (inputNickName.isValidUserNickName()) {
            _validateNickNameState.value = NickNameState.ValidationSuccess
            checkNickname(inputNickName)
        } else {
            _validateNickNameState.value = NickNameState.ValidationError
        }

    }

    /**
     * Bottom Button Click Action
     * @param inputNickName: String 입력받은 닉네임
     * @param isMarketingAgreed: Boolean 마케팅 동의 여부
     */
    private fun onClickSignUp(
        inputNickName: String,
        isMarketingAgreed: Boolean,
    ) {
        viewModelScope.launch {
            signupUseCase(inputNickName, isMarketingAgreed).onStart {
                _loadingUiState.value = true
                _errorUiState.value = null
            }.collectLatest { uiState ->
                _loadingUiState.value = false
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
                        _validateNickNameState.value = NickNameState.Succeed
                    }
                    is UiState.Error -> {
                        _errorUiState.value = uiState.message
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
            checkNickNameUseCase(nickName).onStart{
                _loadingUiState.value = true
                _errorUiState.value = null
            }.collectLatest { uiState ->
                _loadingUiState.value = false
                when (uiState) {
                    is UiState.Success -> {
                        _validateNickNameState.value = NickNameState.ValidationSuccess
                    }
                    is UiState.Error -> {
                        if (uiState.code != null) {
                            when (uiState.code) {
                                400 -> _validateNickNameState.value = NickNameState.DuplicateError
                                else -> _errorUiState.value = uiState.message
                            }
                        } else {
                            _errorUiState.value = uiState.message
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