package com.kyu.jiu_jitsu.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.model.BELT_RANK
import com.kyu.jiu_jitsu.data.model.BELT_STRIPE
import com.kyu.jiu_jitsu.data.model.CommunityProfileInfo
import com.kyu.jiu_jitsu.data.model.GENDER
import com.kyu.jiu_jitsu.data.model.POSITION
import com.kyu.jiu_jitsu.data.model.SUBMISSION
import com.kyu.jiu_jitsu.data.model.TECHNIQUE
import com.kyu.jiu_jitsu.data.model.dto.response.CommunityProfileData
import com.kyu.jiu_jitsu.data.model.dto.response.Competition
import com.kyu.jiu_jitsu.data.model.singleton.ProfileSingleton
import com.kyu.jiu_jitsu.domain.usecase.community.GetCommunityProfileUseCase
import com.kyu.jiu_jitsu.domain.usecase.community.UpdateCommunityProfileUseCase
import com.kyu.jiu_jitsu.domain.usecase.user.GetLocalNickNameUseCase
import com.kyu.jiu_jitsu.profile.model.POSITION_LIST
import com.kyu.jiu_jitsu.profile.model.SUBMISSION_LIST
import com.kyu.jiu_jitsu.profile.model.TECHNIQUE_LIST
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getLocalNickNameUseCase: GetLocalNickNameUseCase,
    private val getCommunityProfile: GetCommunityProfileUseCase,
    private val updateCommunityProfileUseCase: UpdateCommunityProfileUseCase,
): ViewModel() {

    var profileUiState by mutableStateOf<UiState<CommunityProfileInfo>>(UiState.Idle)
    var localNickname by mutableStateOf<String?>(null)

    fun initProfileData() {
        viewModelScope.launch {
            profileUiState = UiState.Loading
            combine(
                getLocalNickNameUseCase(),
                getCommunityProfile()
            ) { nickNameState, uiState ->
                localNickname = nickNameState

                when(uiState) {
                    is UiState.Success -> {
                        ProfileSingleton.profileInfo = uiState.result
                        profileUiState = UiState.Success(uiState.result)
                    }
                    is UiState.Error -> profileUiState = UiState.Error(message = uiState.message, retryable =  false)
                    else -> {}
                }
            }.collectLatest { it ->

            }
//            getCommunityProfile().collectLatest { uiState ->
//                when(uiState) {
//                    is UiState.Success -> profileUiState =  UiState.Success(uiState.result)
//                    is UiState.Error -> profileUiState = UiState.Error(message = uiState.message, retryable =  false)
//                    else -> {}
//                }
//            }
        }

    }

    /** 도장 정보 수정 */
    fun changeAcademyName(academyName: String) {
        changeCommunityProfile(
            academyName = academyName
        )
    }

    /** 벨트/체급 수정 */
    fun changeBeltAndWeight(
        beltRank: BELT_RANK? = null,
        beltStripe: BELT_STRIPE? = null,
        gender: GENDER? = null,
        weightKg: Double? = null,
        isWeightHidden: Boolean? = null,
    ) {
        changeCommunityProfile(
            beltRank = beltRank,
            beltStripe = beltStripe,
            gender = gender,
            weightKg = weightKg,
            isWeightHidden = isWeightHidden,
        )
    }

    private fun changeCommunityProfile(
        nickName: String? = null,
        profileImageUrl: String? = null,
        beltRank: BELT_RANK? = null,
        beltStripe: BELT_STRIPE? = null,
        gender: GENDER? = null,
        weightKg: Double? = null,
        academyName: String? = null,
        competitions: List<Competition>? = null,
        bestSubmission: SUBMISSION? = null,
        favoriteSubmission: SUBMISSION? = null,
        bestTechnique: TECHNIQUE? = null,
        favoriteTechnique: TECHNIQUE? = null,
        bestPosition: POSITION? = null,
        favoritePosition: POSITION? = null,
        isWeightHidden: Boolean? = null,
    ) {
        viewModelScope.launch {
            profileUiState = UiState.Loading
            updateCommunityProfileUseCase(
                CommunityProfileData(
                    nickname = nickName?: ProfileSingleton.profileInfo?.nickname,
                    profileImageUrl = profileImageUrl?: ProfileSingleton.profileInfo?.profileImageUrl,
                    beltRank = beltRank?.name ?: ProfileSingleton.profileInfo?.beltRank?.name,
                    beltStripe = beltStripe?.name ?: ProfileSingleton.profileInfo?.beltStripe?.name,
                    gender = gender?.name ?: ProfileSingleton.profileInfo?.gender?.name,
                    weightKg = weightKg ?: ProfileSingleton.profileInfo?.weightKg,
                    isWeightHidden = isWeightHidden ?: ProfileSingleton.profileInfo?.isWeightHidden,
                    academyName = academyName ?: ProfileSingleton.profileInfo?.academyName,
                    bestSubmission = bestSubmission?.name ?: ProfileSingleton.profileInfo?.bestSubmission?.name,
                    favoriteSubmission = favoriteSubmission?.name ?: ProfileSingleton.profileInfo?.favoriteSubmission?.name,
                    bestTechnique = bestTechnique?.name ?: ProfileSingleton.profileInfo?.bestTechnique?.name,
                    favoriteTechnique = favoriteTechnique?.name ?: ProfileSingleton.profileInfo?.favoriteTechnique?.name,
                    bestPosition = bestPosition?.name ?: ProfileSingleton.profileInfo?.bestPosition?.name,
                    favoritePosition = favoritePosition?.name ?: ProfileSingleton.profileInfo?.favoritePosition?.name,
                )
            ).collectLatest { uiState ->
                when(uiState) {
                    is UiState.Success -> {
                        ProfileSingleton.profileInfo = uiState.result
                        profileUiState = UiState.Success(uiState.result)
                    }
                    is UiState.Error -> profileUiState = UiState.Error(message = uiState.message, retryable =  false)
                    else -> {}
                }
            }
        }
    }

    /** **/
    fun updateProfileMyStyle(
        bestPositionIndex: Int? = null,
        favoritePositionIndex: Int? = null,
        bestTechniqueIndex: Int? = null,
        favoriteTechniqueIndex: Int? = null,
        bestSubmissionIndex: Int? = null,
        favoriteSubmissionIndex: Int? = null,
    ) {
        changeCommunityProfile(
            bestPosition = if(bestPositionIndex == null) null else POSITION_LIST[bestPositionIndex],
            favoritePosition = if(favoritePositionIndex == null) null else POSITION_LIST[favoritePositionIndex],
            bestTechnique = if(bestTechniqueIndex == null) null else TECHNIQUE_LIST[bestTechniqueIndex],
            favoriteTechnique = if(favoriteTechniqueIndex == null) null else TECHNIQUE_LIST[favoriteTechniqueIndex],
            bestSubmission = if(bestSubmissionIndex == null) null else SUBMISSION_LIST[bestSubmissionIndex],
            favoriteSubmission = if(favoriteSubmissionIndex == null) null else SUBMISSION_LIST[favoriteSubmissionIndex],
        )
    }

}