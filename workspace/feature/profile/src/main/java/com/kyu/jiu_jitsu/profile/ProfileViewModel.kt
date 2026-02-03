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
import com.kyu.jiu_jitsu.data.model.dto.request.PROFILE_REQUEST_TYPE
import com.kyu.jiu_jitsu.data.model.dto.request.UpdateCommunityProfileRequest
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
            UpdateCommunityProfileRequest(
                profileRequestType = PROFILE_REQUEST_TYPE.ACADEMY().name,
                academyName = academyName
            )
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
            UpdateCommunityProfileRequest(
                profileRequestType = PROFILE_REQUEST_TYPE.BELT_WEIGHT().name,
                beltRank = beltRank?.name ?: ProfileSingleton.profileInfo?.beltRank?.name,
                beltStripe = beltStripe?.name ?: ProfileSingleton.profileInfo?.beltStripe?.name,
                gender = gender?.name ?: ProfileSingleton.profileInfo?.gender?.name,
                weightKg = weightKg,
                isWeightHidden = isWeightHidden,
            )
        )

    }

    private fun changeCommunityProfile(
        requestData : UpdateCommunityProfileRequest
    ) {
        viewModelScope.launch {
            profileUiState = UiState.Loading
            updateCommunityProfileUseCase(requestData).collectLatest { uiState ->
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
        val bestPosition = if(bestPositionIndex == null) null else POSITION_LIST[bestPositionIndex]
        val favoritePosition = if(favoritePositionIndex == null) null else POSITION_LIST[favoritePositionIndex]
        val bestTechnique = if(bestTechniqueIndex == null) null else TECHNIQUE_LIST[bestTechniqueIndex]
        val favoriteTechnique = if(favoriteTechniqueIndex == null) null else TECHNIQUE_LIST[favoriteTechniqueIndex]
        val bestSubmission = if(bestSubmissionIndex == null) null else SUBMISSION_LIST[bestSubmissionIndex]
        val favoriteSubmission = if(favoriteSubmissionIndex == null) null else SUBMISSION_LIST[favoriteSubmissionIndex]

        changeCommunityProfile(
            UpdateCommunityProfileRequest(
                profileRequestType = PROFILE_REQUEST_TYPE.POSITION().name,
                bestSubmission = bestSubmission?.name ?: ProfileSingleton.profileInfo?.bestSubmission?.name,
                favoriteSubmission = favoriteSubmission?.name ?: ProfileSingleton.profileInfo?.favoriteSubmission?.name,
            )
        )
    }

}