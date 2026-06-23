package com.kyu.jiu_jitsu.profile.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.model.BELT_RANK
import com.kyu.jiu_jitsu.data.model.BELT_STRIPE
import com.kyu.jiu_jitsu.data.model.CdnImageInfo
import com.kyu.jiu_jitsu.data.model.CommunityProfileInfo
import com.kyu.jiu_jitsu.data.model.GENDER
import com.kyu.jiu_jitsu.data.model.dto.request.PROFILE_REQUEST_TYPE
import com.kyu.jiu_jitsu.data.model.dto.request.UpdateCommunityProfileRequest
import com.kyu.jiu_jitsu.data.model.singleton.ProfileSingleton
import com.kyu.jiu_jitsu.domain.usecase.community.GetCommunityProfileUseCase
import com.kyu.jiu_jitsu.domain.usecase.community.UpdateCommunityProfileUseCase
import com.kyu.jiu_jitsu.domain.usecase.image.UploadCommunityImageUseCase
import com.kyu.jiu_jitsu.domain.usecase.local.GetLocalNickNameUseCase
import com.kyu.jiu_jitsu.profile.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileAction {
    data object FetchProfileData: ProfileAction
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getLocalNickNameUseCase: GetLocalNickNameUseCase,
    private val getCommunityProfile: GetCommunityProfileUseCase,
    private val updateCommunityProfileUseCase: UpdateCommunityProfileUseCase,
    private val uploadCommunityImageUseCase: UploadCommunityImageUseCase,
): ViewModel() {

    var profileUiState by mutableStateOf<UiState<CommunityProfileInfo>>(UiState.Idle)
    var localNickname by mutableStateOf<String?>(null)

    private var _errorUiState = MutableStateFlow<String?>(null)
    var errorUiState = _errorUiState.asStateFlow()

    private var _loadingUiState = MutableStateFlow(false)
    var loadingUiState = _loadingUiState.asStateFlow()

    private var _profileInfoUiState = MutableStateFlow<CommunityProfileInfo?>(null)
    var profileInfoUiState = _profileInfoUiState.asStateFlow()

    private var _profileImageUploadUiState = MutableStateFlow<UiState<CdnImageInfo>>(UiState.Idle)
    var profileImageUploadUiState = _profileImageUploadUiState.asStateFlow()

    init {
        initProfileData()
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.FetchProfileData -> { initProfileData() }
        }
    }

    fun uploadCommunityImage(imageUri: Uri) {
        val publicKey = BuildConfig.IMAGE_PUBLIC_KEY
        if (publicKey.isBlank()) {
            val errorMessage = "IMAGE_PUBLIC_KEY가 설정되어 있지 않습니다."
            _profileImageUploadUiState.value = UiState.Error(
                message = errorMessage,
                retryable = false,
            )
            _errorUiState.value = errorMessage
            return
        }

        viewModelScope.launch {
            // 5번 사용자 프로필 이미지 업데이트는 아직 수행하지 않는다.
            // 여기서는 CDN 인증값 요청 -> ImageKit 직접 업로드 -> 내 서버 이미지 TEMP 등록까지만 완료하고,
            // 등록 결과의 imageUrl은 다음 단계에서 user/profile/image API에 연결할 수 있도록 state로 노출한다.
            uploadCommunityImageUseCase(
                imageUri = imageUri.toString(),
                publicKey = publicKey,
            ).onStart {
                _loadingUiState.value = true
                _errorUiState.value = null
                _profileImageUploadUiState.value = UiState.Loading
            }.collectLatest { uiState ->
                _loadingUiState.value = false
                _profileImageUploadUiState.value = uiState

                if (uiState is UiState.Error) {
                    _errorUiState.value = uiState.message
                }
            }
        }
    }

    private fun initProfileData() {
        viewModelScope.launch {
            profileUiState = UiState.Loading
//            combine(
//                getLocalNickNameUseCase(),
//                getCommunityProfile()
//            ) { nickNameState, uiState ->
//                localNickname = nickNameState
//
//                when(uiState) {
//                    is UiState.Success -> {
//                        ProfileSingleton.profileInfo = uiState.result
//                        profileUiState = UiState.Success(uiState.result)
//                    }
//                    is UiState.Error -> {
//                        _errorUiState.value = uiState.message
//                    }
//                    else -> {}
//                }
//            }.collect()

            getCommunityProfile().onStart {
                _loadingUiState.value = true
                _errorUiState.value = null
            }.collectLatest { uiState ->
                _loadingUiState.value = false
                when(uiState) {
                    is UiState.Success -> {
                        ProfileSingleton.profileInfo = uiState.result
                        _profileInfoUiState.value = uiState.result
                    }
                    is UiState.Error -> _errorUiState.value = uiState.message
                    else -> {}
                }
            }
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
            updateCommunityProfileUseCase(requestData).onStart {
                _loadingUiState.value = true
                _errorUiState.value = null
            }.collectLatest { uiState ->
                _loadingUiState.value = false
                when(uiState) {
                    is UiState.Success -> {
                        ProfileSingleton.profileInfo = uiState.result
                        _profileInfoUiState.value = uiState.result
                    }
                    is UiState.Error -> _errorUiState.value = uiState.message
                    else -> {}
                }
            }
        }
    }

}
