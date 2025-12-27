package com.kyu.jiu_jitsu.data.model.singleton

import com.kyu.jiu_jitsu.data.model.CommunityProfileInfo
import com.kyu.jiu_jitsu.data.model.isShowModify

object ProfileSingleton {
    var profileInfo: CommunityProfileInfo? = null

    var getBeltRank = profileInfo?.beltRank
    var isShowModifyBtn = profileInfo?.isShowModify() ?: false
    var getNickName = profileInfo?.nickname ?: ""
    var getProfileImage = profileInfo?.profileImageUrl ?: ""
    var getAcademyName = profileInfo?.academyName ?: ""

    fun clearProfileData() {
        profileInfo = null
    }

}