package com.kyu.jiu_jitsu.profile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyu.jiu_jitsu.data.repository.CommunityRepository
import com.kyu.jiu_jitsu.model.CommunityProfileField
import com.kyu.jiu_jitsu.model.CommunityProfileInfo
import com.kyu.jiu_jitsu.model.CommunityProfileUpdate
import com.kyu.jiu_jitsu.ui.state.UiState
import com.kyu.jiu_jitsu.ui.state.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ModifyAcademyAction {
    data class ChangeAcademyName(val academyName: String, val onCompleted: () -> Unit = {}): ModifyAcademyAction
}

@HiltViewModel
class ModifyAcademyViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
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

            val update = CommunityProfileUpdate(
                field = CommunityProfileField.ACADEMY,
                academyName = academyName,
            )

            // Repository updates its shared StateFlow before returning success, so the main
            // profile screen will already have the latest server snapshot when navigation pops.
            profileUiState = communityRepository.modifyCommunityProfile(update).toUiState()
            if (profileUiState is UiState.Success) onCompleted()
        }
    }

}
