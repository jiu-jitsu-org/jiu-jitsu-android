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
import com.kyu.jiu_jitsu.data.model.dto.response.Competition
import com.kyu.jiu_jitsu.data.model.singleton.ProfileSingleton
import com.kyu.jiu_jitsu.domain.usecase.community.UpdateCommunityProfileUseCase
import com.kyu.jiu_jitsu.profile.screen.CompetitionScreenType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ModifyCompetitionAction {
    /** 상단 뒤로 가기 버튼 액션 **/
    data class BackClicked(val popNavigation: () -> Unit): ModifyCompetitionAction
    /** 하단 버튼 액션 **/
    data class BottomBtnClicked(val onCompleteClick: () -> Unit = {}): ModifyCompetitionAction
}

@HiltViewModel
class ModifyCompetitionViewModel @Inject constructor(
    private val updateCommunityProfileUseCase: UpdateCommunityProfileUseCase,
): ViewModel()  {

    var profileUiState by mutableStateOf<UiState<CommunityProfileInfo>>(UiState.Idle)

    /** Screen Index **/
    var screenIndex by mutableStateOf<CompetitionScreenType>(CompetitionScreenType.TypeDate)
    /** Selected Item **/
    var selectedYear by mutableStateOf<Int?>(null)
    var selectedMonth by mutableStateOf<Int?>(null)
    var selectedName by mutableStateOf<String?>(null)
    var selectedRank by mutableStateOf<String?>(null)

    fun onAction(action: ModifyCompetitionAction) {
        when (action) {
            is ModifyCompetitionAction.BackClicked -> onBackArrowClick(action.popNavigation)
            is ModifyCompetitionAction.BottomBtnClicked -> onBottomBtnClick(action.onCompleteClick)
        }

    }

    // 상단 뒤로가기 클릭
    private fun onBackArrowClick(popNavigation: () -> Unit) {
        when(screenIndex) {
            CompetitionScreenType.TypeDate -> {
                popNavigation()
            }
            CompetitionScreenType.TypeTitle -> {
                screenIndex = CompetitionScreenType.TypeDate
            }
            CompetitionScreenType.TypeResult -> {
                screenIndex = CompetitionScreenType.TypeTitle
            }
        }
    }

    // 하단 버튼 클릭
    private fun onBottomBtnClick(onCompleteClick: () -> Unit) {
        when(screenIndex) {
            CompetitionScreenType.TypeDate -> {
                screenIndex = CompetitionScreenType.TypeTitle
            }
            CompetitionScreenType.TypeTitle -> {
                screenIndex = CompetitionScreenType.TypeResult
            }
            CompetitionScreenType.TypeResult -> {
                viewModelScope.launch {
                    profileUiState = UiState.Loading

                    val requestData = UpdateCommunityProfileRequest(
                        profileRequestType = PROFILE_REQUEST_TYPE.COMPETITION().name,
                        competitionInfoList = listOf(
                            Competition(
                                competitionYear = selectedYear,
                                competitionMonth = selectedMonth,
                                competitionName = selectedName,
                                competitionRank = selectedRank
                            )
                        )
                    )

                    updateCommunityProfileUseCase(requestData).collectLatest { uiState ->
                        when(uiState) {
                            is UiState.Success -> {
                                ProfileSingleton.profileInfo = uiState.result
                                profileUiState = UiState.Success(uiState.result)

                                onCompleteClick()
                            }
                            is UiState.Error -> profileUiState = UiState.Error(message = uiState.message, retryable =  false)
                            else -> {}
                        }
                    }
                }
            }
        }
    }

}