package com.kyu.jiu_jitsu.profile.model

import com.kyu.jiu_jitsu.data.model.CommunityProfileInfo

data class ProfileUiState(
    var nickName: String = "",
    var profileImageUrl: String = "",
    var communityProfileInfo: CommunityProfileInfo?
)

