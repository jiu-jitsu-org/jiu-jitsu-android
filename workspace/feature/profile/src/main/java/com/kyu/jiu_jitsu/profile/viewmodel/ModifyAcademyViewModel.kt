package com.kyu.jiu_jitsu.profile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.model.CommunityProfileInfo
import com.kyu.jiu_jitsu.data.model.dto.request.PROFILE_REQUEST_TYPE
import com.kyu.jiu_jitsu.data.model.dto.request.UpdateCommunityProfileRequest
import com.kyu.jiu_jitsu.data.model.singleton.ProfileSingleton
import com.kyu.jiu_jitsu.domain.usecase.community.UpdateCommunityProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ModifyAcademyAction {
    data class ChangeAcademyName(val academyName: String, val onCompleted: () -> Unit = {}): ModifyAcademyAction
}

@HiltViewModel
class ModifyAcademyViewModel @Inject constructor(
    private val updateCommunityProfileUseCase: UpdateCommunityProfileUseCase,
): ViewModel() {

    var profileUiState by mutableStateOf<UiState<CommunityProfileInfo>>(UiState.Idle)

    fun onAction(action: ModifyAcademyAction) {
        when (action) {
            is ModifyAcademyAction.ChangeAcademyName -> changeAcademyName(action.academyName, action.onCompleted)
        }
    }

    /** 도장 정보 수정 */
    fun changeAcademyName(academyName: String, onCompleted: () -> Unit = {}) {
        viewModelScope.launch {
            profileUiState = UiState.Loading

            val requestData = UpdateCommunityProfileRequest(
                profileRequestType = PROFILE_REQUEST_TYPE.ACADEMY().name,
                academyName = academyName
            )

            updateCommunityProfileUseCase(requestData).collectLatest { uiState ->
                when(uiState) {
                    is UiState.Success -> {
                        ProfileSingleton.profileInfo = uiState.result
                        profileUiState = UiState.Success(uiState.result)

                        onCompleted()

                    }
                    is UiState.Error -> profileUiState = UiState.Error(message = uiState.message, retryable =  false)
                    else -> {}
                }
            }
        }
    }

}