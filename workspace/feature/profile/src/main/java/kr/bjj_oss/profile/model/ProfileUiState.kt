package kr.bjj_oss.profile.model

import kr.bjj_oss.model.CommunityProfileInfo

data class ProfileUiState(
    var nickName: String = "",
    var profileImageUrl: String = "",
    var communityProfileInfo: CommunityProfileInfo?
)
