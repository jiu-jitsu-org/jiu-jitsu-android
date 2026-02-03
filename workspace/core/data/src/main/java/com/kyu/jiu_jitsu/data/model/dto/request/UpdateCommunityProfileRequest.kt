package com.kyu.jiu_jitsu.data.model.dto.request

import com.kyu.jiu_jitsu.data.model.dto.response.Competition
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateCommunityProfileRequest(
    val profileRequestType: String? = null,
    val nickname: String? = null,
    val profileImageUrl: String? = null,
    val beltRank: String? = null,
    val beltStripe: String? = null,
    val gender: String? = null,
    val weightKg: Double? = null,
    val academyName: String? = null,
    val competitions: List<Competition>? = null,
    val bestSubmission: String? = null,
    val favoriteSubmission: String? = null,
    val bestTechnique: String? = null,
    val favoriteTechnique: String? = null,
    val bestPosition: String? = null,
    val favoritePosition: String? = null,
    val isWeightHidden: Boolean? = null,
    val isOwner: Boolean? = null,
    val teachingPhilosophy: String? = null,
    val teachingStartDate: String? = null,
    val teachingDetail: String? = null,
)

sealed class PROFILE_REQUEST_TYPE(open val name: String) {
    data class ACADEMY(override val name: String = "ACADEMY") : PROFILE_REQUEST_TYPE(name)  // 도장정보 도장명
    data class BELT_WEIGHT(override val name: String = "BELT_WEIGHT") : PROFILE_REQUEST_TYPE(name) // 벨트 성별 체급
    data class POSITION(override val name: String = "POSITION"): PROFILE_REQUEST_TYPE(name) // 포지션
    data class TECHNIQUE(override val name: String = "TECHNIQUE"): PROFILE_REQUEST_TYPE(name)   // 기술
    data class SUBMISSION(override val name: String = "SUBMISSION"): PROFILE_REQUEST_TYPE(name) // 서브미션
    data class COMPETITION(override val name: String = "COMPETITION"): PROFILE_REQUEST_TYPE(name) // 대회정보
    data class OWNER_INFO(override val name: String = "OWNER_INFO"): PROFILE_REQUEST_TYPE(name) // 관장 사범 정보
}

