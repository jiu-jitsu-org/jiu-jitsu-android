package kr.bjj_oss.profile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.bjj_oss.data.repository.CommunityRepository
import kr.bjj_oss.model.CommunityProfileField
import kr.bjj_oss.model.CommunityProfileInfo
import kr.bjj_oss.model.CommunityProfileUpdate
import kr.bjj_oss.model.CompetitionInfo
import kr.bjj_oss.model.toCompetitionRank
import kr.bjj_oss.profile.screen.CompetitionScreenType
import kr.bjj_oss.ui.state.UiState
import kr.bjj_oss.ui.state.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val communityRepository: CommunityRepository,
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

                    val update = CommunityProfileUpdate(
                        field = CommunityProfileField.COMPETITION,
                        competitions = listOf(
                            CompetitionInfo(
                                competitionYear = selectedYear,
                                competitionMonth = selectedMonth,
                                competitionName = selectedName,
                                competitionRank = selectedRank.toCompetitionRank(),
                            ),
                        ),
                    )

                    profileUiState = communityRepository.modifyCommunityProfile(update).toUiState()
                    if (profileUiState is UiState.Success) onCompleteClick()
                }
            }
        }
    }

}
