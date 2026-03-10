package com.kyu.jiu_jitsu.profile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.kyu.jiu_jitsu.profile.model.POSITION_LIST
import com.kyu.jiu_jitsu.profile.model.SUBMISSION_LIST
import com.kyu.jiu_jitsu.profile.model.TECHNIQUE_LIST
import com.kyu.jiu_jitsu.profile.model.getIndex
import com.kyu.jiu_jitsu.ui.routes.SkillStyleScreenType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ModifyMyStyleAction {
    /** 나의 스타일 나중에 등록 하기 **/
    data class SkipClicked(val completeClick: () -> Unit): ModifyMyStyleAction
    /** 하단 수정 또는 설정 버튼 **/
    data class BottomBtnClicked(val completeClick: () -> Unit): ModifyMyStyleAction
    /** 스타일 카드 선택 **/
    data class CardItemSelected(val selectedIndex: Int): ModifyMyStyleAction
    /** 뒤로 가기 액션 **/
    data class BackClicked(val navigationBackAction: () -> Unit): ModifyMyStyleAction
}

@HiltViewModel
class ModifyMyStyleViewModel @Inject constructor(
    private val updateCommunityProfileUseCase: UpdateCommunityProfileUseCase,
): ViewModel()  {

    var profileUiState by mutableStateOf<UiState<CommunityProfileInfo>>(UiState.Idle)

    /** SkillStyleScreenType **/
    var screenType by mutableStateOf<SkillStyleScreenType>(SkillStyleScreenType.ALL)

    /** Tab Index ( 0 == Best, 1 == Favorite ) **/
    var styleTabIndex by mutableIntStateOf(0)
    var bestTabTitle by mutableStateOf<String?>(null)
    var favoriteTabTitle by mutableStateOf<String?>(null)

    /** Page Index ( 0==Position, 1==Technique, 2==Submission) **/
    var pageIndex by mutableIntStateOf(0)

    /** Select Position Value **/
    var bestPositionIndex by mutableStateOf<Int?>(null)
    var favoritePositionIndex by mutableStateOf<Int?>(null)

    /** Select Technique Value **/
    var bestTechniqueIndex by mutableStateOf<Int?>(null)
    var favoriteTechniqueIndex by mutableStateOf<Int?>(null)

    /** Select Submission Value **/
    var bestSubmissionIndex by mutableStateOf<Int?>(null)
    var favoriteSubmissionIndex by mutableStateOf<Int?>(null)

    fun onAction(action: ModifyMyStyleAction) {
        when (action) {
            is ModifyMyStyleAction.SkipClicked -> changeUiIndex(action.completeClick)
            is ModifyMyStyleAction.BottomBtnClicked -> updateProfileMyStyle(action.completeClick)
            is ModifyMyStyleAction.CardItemSelected -> onSelectedCardItem(action.selectedIndex)
            is ModifyMyStyleAction.BackClicked -> onHistoryBackClick(action.navigationBackAction)
        }
    }

    fun initScreenType(type: String) {
        // 스크린 타입 - 포지션, 기술, 서브미션
        viewModelScope.launch {
            when (type) {
                SkillStyleScreenType.Position.screenName -> {
                    pageIndex = 0
                    screenType = SkillStyleScreenType.Position

                    bestPositionIndex = ProfileSingleton.profileInfo?.bestPosition?.getIndex()
                    favoritePositionIndex = ProfileSingleton.profileInfo?.favoritePosition?.getIndex()
                }

                SkillStyleScreenType.Technique.screenName -> {
                    pageIndex = 1
                    screenType = SkillStyleScreenType.Technique

                    bestTechniqueIndex = ProfileSingleton.profileInfo?.bestTechnique?.getIndex()
                    favoriteTechniqueIndex = ProfileSingleton.profileInfo?.favoriteTechnique?.getIndex()
                }

                SkillStyleScreenType.Submission.screenName -> {
                    pageIndex = 2
                    screenType = SkillStyleScreenType.Submission

                    bestSubmissionIndex = ProfileSingleton.profileInfo?.bestSubmission?.getIndex()
                    favoriteSubmissionIndex = ProfileSingleton.profileInfo?.favoriteSubmission?.getIndex()
                }

                else -> {
                    // ALL > 포지션, 기술, 서브미션 모두 선택 > 시작은 포지션부터
                    pageIndex = 0
                    screenType = SkillStyleScreenType.ALL
                }
            }
            setTabTitle()
        }
    }

    /** 상단 탭 타이틀  **/
    fun setTabTitle() {
        viewModelScope.launch {
            when(pageIndex) {
                0 -> { // POSITION
                    bestTabTitle = POSITION_LIST[bestPositionIndex?:0].displayName
                    if (styleTabIndex == 0) {
                        favoriteTabTitle = if(favoritePositionIndex == null) "입력해주세요" else POSITION_LIST[favoritePositionIndex?:0].displayName
                    } else {
                        favoriteTabTitle = POSITION_LIST[favoritePositionIndex?:0].displayName
                        if (favoritePositionIndex == null)
                            favoritePositionIndex = 0
                    }
                }
                1 -> { // TECHNIQUE
                    bestTabTitle = TECHNIQUE_LIST[bestTechniqueIndex?:0].displayName
                    if (styleTabIndex == 0) {
                        favoriteTabTitle = if(favoriteTechniqueIndex == null) "입력해주세요" else TECHNIQUE_LIST[favoriteTechniqueIndex?:0].displayName
                    } else {
                        favoriteTabTitle = TECHNIQUE_LIST[favoriteTechniqueIndex?:0].displayName
                        if (favoriteTechniqueIndex == null)
                            favoriteTechniqueIndex = 0
                    }
                }
                2 -> { // SUBMISSION
                    bestTabTitle = SUBMISSION_LIST[bestSubmissionIndex?:0].displayName
                    if (styleTabIndex == 0) {
                        favoriteTabTitle = if(favoriteSubmissionIndex == null) "입력해주세요" else SUBMISSION_LIST[favoriteSubmissionIndex?:0].displayName
                    } else {
                        favoriteTabTitle = SUBMISSION_LIST[favoriteSubmissionIndex?:0].displayName
                        if (favoriteSubmissionIndex == null)
                            favoriteSubmissionIndex = 0
                    }
                }
            }
        }
    }

    /** 카드 선택 콜백 **/
    private fun onSelectedCardItem(index: Int) {
        when (pageIndex) {
            0 -> {
                if (styleTabIndex == 0) {
                    bestPositionIndex = index
                } else {
                    favoritePositionIndex = index
                }
            }
            1 -> {
                if (styleTabIndex == 0) {
                    bestTechniqueIndex = index
                } else {
                    favoriteTechniqueIndex = index
                }
            }
            2 -> {
                if (styleTabIndex == 0) {
                    bestSubmissionIndex = index
                } else {
                    favoriteSubmissionIndex = index
                }
            }
        }
        setTabTitle()
    }

    /** 뒤로가기 컨트롤 **/
    private fun onHistoryBackClick(navigationBackAction: () -> Unit) {
        viewModelScope.launch {
            if (screenType is SkillStyleScreenType.ALL) {
                // ALL > 포지션, 기술, 서브미션 모두 선택 > 시작은 포지션부터
                when (pageIndex) {
                    1 -> { pageIndex = 0 }
                    2 -> { pageIndex = 1 }
                    else -> { navigationBackAction() }
                }
            } else {
                navigationBackAction()
            }
        }
    }

    /** UI Index 변경
     * ex. 특기 > 최애 or 최애 > 다음 특기 **/
    private fun changeUiIndex(completeClick: () -> Unit) {
        viewModelScope.launch {
            if (screenType == SkillStyleScreenType.ALL) {
                if (styleTabIndex == 1) {
                    // 스타일 인덱스 마지막 값(최애 탭) >> 페이지 인덱스 변경
                    pageIndex++
                } else {
                    if (pageIndex == 2) {
                        // 페이지 인덱스 마지막
                        completeClick()
                    } else {
                        // 스타일 인덱스(특기->최애) 변경
                        styleTabIndex++
                    }
                }
            } else {
                if (styleTabIndex == 1) {
                    // 스타일 인덱스 마지막 값(최애 탭) >> 수정 하기
                    completeClick()
                } else {
                    // 스타일 인덱스(특기->최애) 변경
                    styleTabIndex++
                }
            }
        }
    }

    /** Update Profile MyStyle
     * 내 스타일 등록 (포지션, 기술, 서브미션) **/
    private fun updateProfileMyStyle(completeClick: () -> Unit) {
        viewModelScope.launch {
            profileUiState = UiState.Loading

            val requestData = UpdateCommunityProfileRequest()
            var profileRequestType = ""
            when(pageIndex) {
                0 -> { // POSITION
                    if (styleTabIndex == 0) {
                        profileRequestType = PROFILE_REQUEST_TYPE.POSITION_BEST().name
                        requestData.bestPosition = POSITION_LIST[bestPositionIndex?:0].name
                    } else {
                        profileRequestType = PROFILE_REQUEST_TYPE.POSITION_FAVORITE().name
                        requestData.favoritePosition = POSITION_LIST[favoritePositionIndex?:0].name
                    }
                }
                1 -> { // TECHNIQUE
                    if (styleTabIndex == 0) {
                        profileRequestType = PROFILE_REQUEST_TYPE.TECHNIQUE_BEST().name
                        requestData.bestTechnique = TECHNIQUE_LIST[bestTechniqueIndex?:0].name
                    } else {
                        profileRequestType = PROFILE_REQUEST_TYPE.TECHNIQUE_FAVORITE().name
                        requestData.favoriteTechnique = TECHNIQUE_LIST[favoriteTechniqueIndex?:0].name
                    }
                }
                2 -> { // SUBMISSION
                    if (styleTabIndex == 0) {
                        profileRequestType = PROFILE_REQUEST_TYPE.SUBMISSION_BEST().name
                        requestData.bestSubmission = SUBMISSION_LIST[bestSubmissionIndex?:0].name
                    } else {
                        profileRequestType = PROFILE_REQUEST_TYPE.SUBMISSION_FAVORITE().name
                        requestData.favoriteSubmission = SUBMISSION_LIST[favoriteSubmissionIndex?:0].name
                    }
                }
            }

            requestData.profileRequestType = profileRequestType

            updateCommunityProfileUseCase(requestData).collectLatest { uiState ->
                when(uiState) {
                    is UiState.Success -> {
                        ProfileSingleton.profileInfo = uiState.result
                        profileUiState = UiState.Success(uiState.result)

                        changeUiIndex(completeClick)
                    }
                    is UiState.Error -> profileUiState = UiState.Error(message = uiState.message, retryable =  false)
                    else -> {}
                }
            }
        }
    }
}