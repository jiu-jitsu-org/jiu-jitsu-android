package com.kyu.jiu_jitsu.nickname

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyu.jiu_jitsu.data.repository.SessionRepository
import com.kyu.jiu_jitsu.data.repository.UserRepository
import com.kyu.jiu_jitsu.domain.isValidUserNickName
import com.kyu.jiu_jitsu.model.AppResult
import com.kyu.jiu_jitsu.model.SessionUpdate
import com.kyu.jiu_jitsu.nickname.model.NickNameState
import com.kyu.jiu_jitsu.ui.state.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface NickNameAction {
    data class SignUp(val inputNickName: String, val isMarketingAgreed: Boolean) : NickNameAction
    data class ValidateNickName(val inputNickName: String) : NickNameAction
    data object InitNickNameState : NickNameAction
}

/** Handles local syntax validation, server duplication checks, and final account creation. */
@HiltViewModel
class NickNameViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
) : ViewModel() {
    private val mutableLocalNickNameState = MutableStateFlow<String?>(null)
    val localNickNameState = mutableLocalNickNameState.asStateFlow()

    private val mutableValidateNickNameState = MutableStateFlow<NickNameState>(NickNameState.Idle)
    val validateNickNameState = mutableValidateNickNameState.asStateFlow()

    private val mutableErrorUiState = MutableStateFlow<String?>(null)
    val errorUiState = mutableErrorUiState.asStateFlow()

    private val mutableLoadingUiState = MutableStateFlow(false)
    val loadingUiState = mutableLoadingUiState.asStateFlow()

    init {
        getLocalNickName()
    }

    fun onAction(action: NickNameAction) {
        when (action) {
            is NickNameAction.SignUp -> signUp(action.inputNickName, action.isMarketingAgreed)
            is NickNameAction.ValidateNickName -> validateNickname(action.inputNickName)
            NickNameAction.InitNickNameState -> mutableValidateNickNameState.value = NickNameState.Idle
        }
    }

    /** Reads the initial value once; this screen does not need a permanent DataStore collector. */
    private fun getLocalNickName() {
        viewModelScope.launch {
            mutableLoadingUiState.value = true
            mutableLocalNickNameState.value = sessionRepository.nickname.first()
            mutableLoadingUiState.value = false
        }
    }

    private fun validateNickname(inputNickName: String) {
        if (!inputNickName.isValidUserNickName()) {
            mutableValidateNickNameState.value = NickNameState.ValidationError
            return
        }

        // Local validation gives immediate feedback; the backend remains authoritative for
        // uniqueness, so ValidationSuccess is finalized by checkNickname below.
        checkNickname(inputNickName)
    }

    private fun signUp(inputNickName: String, isMarketingAgreed: Boolean) {
        viewModelScope.launch {
            beginRequest()
            when (val result = userRepository.signupUser(inputNickName, isMarketingAgreed)) {
                is AppResult.Success -> {
                    val loginInfo = result.data
                    sessionRepository.updateSession(
                        SessionUpdate(
                            accessToken = loginInfo.accessToken,
                            refreshToken = loginInfo.refreshToken,
                            nickname = loginInfo.userInfo?.nickname ?: inputNickName,
                            profileImageUrl = loginInfo.userInfo?.profileImageUrl,
                        ),
                    )
                    mutableValidateNickNameState.value = NickNameState.Succeed
                }

                is AppResult.Failure -> {
                    mutableErrorUiState.value = result.error.toUiError().message
                }
            }
            mutableLoadingUiState.value = false
        }
    }

    private fun checkNickname(nickName: String) {
        viewModelScope.launch {
            beginRequest()
            when (val result = userRepository.checkNickname(nickName)) {
                is AppResult.Success -> {
                    mutableValidateNickNameState.value = if (result.data) {
                        NickNameState.ValidationSuccess
                    } else {
                        NickNameState.DuplicateError
                    }
                }

                is AppResult.Failure -> {
                    val error = result.error.toUiError()
                    if (error.code == 400) {
                        mutableValidateNickNameState.value = NickNameState.DuplicateError
                    } else {
                        mutableErrorUiState.value = error.message
                    }
                }
            }
            mutableLoadingUiState.value = false
        }
    }

    private fun beginRequest() {
        mutableLoadingUiState.value = true
        mutableErrorUiState.value = null
    }
}
