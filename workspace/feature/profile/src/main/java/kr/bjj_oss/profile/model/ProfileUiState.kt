package kr.bjj_oss.profile.model

import kr.bjj_oss.model.CommunityProfileInfo
import kr.bjj_oss.model.UserProfileInfo

data class ProfileUiState(
    val isGuest: Boolean = true,
    val sessionReady: Boolean = false,
    val sessionNickname: String = "",
    val community: CommunityProfileInfo? = null,
    val user: UserProfileInfo? = null,
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val saving: Boolean = false,
    val imageOperation: ImageOperation? = null,
    val loadFailed: Boolean = false,
    val orderDraft: List<String>? = null,
) {
    val nickname get() = community?.nickname?.takeIf { it.isNotBlank() } ?: sessionNickname
    val showsDefault get() = community?.let {
        it.favoritePosition == null && it.favoriteTechnique == null && it.favoriteSubmission == null && it.competitions.isNullOrEmpty()
    } ?: true
    val busy get() = saving || imageOperation != null
}

enum class ImageOperation { UPLOAD, DELETE, VERIFICATION }
