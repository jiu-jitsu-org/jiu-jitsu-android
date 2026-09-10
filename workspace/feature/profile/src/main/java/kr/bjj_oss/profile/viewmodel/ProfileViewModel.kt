package kr.bjj_oss.profile.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.bjj_oss.data.repository.CommunityRepository
import kr.bjj_oss.data.repository.ImageRepository
import kr.bjj_oss.model.AppResult
import kr.bjj_oss.model.BELT_RANK
import kr.bjj_oss.model.BELT_STRIPE
import kr.bjj_oss.model.CdnImageInfo
import kr.bjj_oss.model.CommunityProfileField
import kr.bjj_oss.model.CommunityProfileInfo
import kr.bjj_oss.model.CommunityProfileUpdate
import kr.bjj_oss.model.GENDER
import kr.bjj_oss.profile.BuildConfig
import kr.bjj_oss.ui.state.UiState
import kr.bjj_oss.ui.state.toUiError
import kr.bjj_oss.ui.state.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileAction {
    data object FetchProfileData : ProfileAction
}

/** Profile screen coordinator backed by the repository's observable profile source of truth. */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    var profileUiState by mutableStateOf<UiState<CommunityProfileInfo>>(UiState.Idle)
        private set

    private val mutableErrorUiState = MutableStateFlow<String?>(null)
    val errorUiState = mutableErrorUiState.asStateFlow()

    private val mutableLoadingUiState = MutableStateFlow(false)
    val loadingUiState = mutableLoadingUiState.asStateFlow()

    // Every profile editor writes through the same repository. Exposing that repository-owned
    // StateFlow means returning from an editor immediately renders its latest server snapshot.
    val profileInfoUiState = communityRepository.communityProfile

    private val mutableProfileImageUploadUiState =
        MutableStateFlow<UiState<CdnImageInfo>>(UiState.Idle)
    val profileImageUploadUiState = mutableProfileImageUploadUiState.asStateFlow()

    init {
        fetchProfileData()
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.FetchProfileData -> fetchProfileData()
        }
    }

    /** Executes the complete image workflow owned by ImageRepository. */
    fun uploadCommunityImage(imageUri: Uri) {
        val publicKey = BuildConfig.IMAGE_PUBLIC_KEY
        if (publicKey.isBlank()) {
            val errorMessage = "IMAGE_PUBLIC_KEY가 설정되어 있지 않습니다."
            mutableProfileImageUploadUiState.value = UiState.Error(
                message = errorMessage,
                retryable = false,
            )
            mutableErrorUiState.value = errorMessage
            return
        }

        viewModelScope.launch {
            beginRequest()
            mutableProfileImageUploadUiState.value = UiState.Loading

            val state = imageRepository.uploadCommunityImage(
                imageUri = imageUri.toString(),
                publicKey = publicKey,
            ).toUiState()
            mutableProfileImageUploadUiState.value = state
            if (state is UiState.Error) mutableErrorUiState.value = state.message

            mutableLoadingUiState.value = false
        }
    }

    private fun fetchProfileData() {
        viewModelScope.launch {
            beginRequest()
            profileUiState = UiState.Loading
            profileUiState = communityRepository.getCommunityProfile().toUiState()
            if (profileUiState is UiState.Error) {
                mutableErrorUiState.value = (profileUiState as UiState.Error).message
            }
            mutableLoadingUiState.value = false
        }
    }

    /** Updates only the academy field while preserving the backend partial-update contract. */
    fun changeAcademyName(academyName: String) {
        changeCommunityProfile(
            CommunityProfileUpdate(
                field = CommunityProfileField.ACADEMY,
                academyName = academyName,
            ),
        )
    }

    /**
     * Updates the belt section.
     *
     * Unchanged selector values are copied from the repository snapshot because this backend
     * operation replaces the whole BELT_WEIGHT section rather than patching individual fields.
     */
    fun changeBeltAndWeight(
        beltRank: BELT_RANK? = null,
        beltStripe: BELT_STRIPE? = null,
        gender: GENDER? = null,
        weightKg: Double? = null,
        isWeightHidden: Boolean? = null,
    ) {
        val currentProfile = communityRepository.communityProfile.value
        changeCommunityProfile(
            CommunityProfileUpdate(
                field = CommunityProfileField.BELT_WEIGHT,
                beltRank = beltRank ?: currentProfile?.beltRank,
                beltStripe = beltStripe ?: currentProfile?.beltStripe,
                gender = gender ?: currentProfile?.gender,
                weightKg = weightKg,
                isWeightHidden = isWeightHidden,
            ),
        )
    }

    private fun changeCommunityProfile(update: CommunityProfileUpdate) {
        viewModelScope.launch {
            beginRequest()
            profileUiState = UiState.Loading
            when (val result = communityRepository.modifyCommunityProfile(update)) {
                is AppResult.Success -> profileUiState = UiState.Success(result.data)
                is AppResult.Failure -> {
                    val error = result.error.toUiError()
                    profileUiState = error
                    mutableErrorUiState.value = error.message
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
