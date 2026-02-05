package com.kyu.jiu_jitsu.data.model.dto.request

import com.kyu.jiu_jitsu.data.model.dto.response.Competition
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateCommunityProfileRequest(
    var profileRequestType: String? = null,
    val nickname: String? = null,
    val profileImageUrl: String? = null,
    val beltRank: String? = null,
    val beltStripe: String? = null,
    val gender: String? = null,
    val weightKg: Double? = null,
    val academyName: String? = null,
    val competitions: List<Competition>? = null,
    var bestSubmission: String? = null,
    var favoriteSubmission: String? = null,
    var bestTechnique: String? = null,
    var favoriteTechnique: String? = null,
    var bestPosition: String? = null,
    var favoritePosition: String? = null,
    val isWeightHidden: Boolean? = null,
    val isOwner: Boolean? = null,
    val teachingPhilosophy: String? = null,
    val teachingStartDate: String? = null,
    val teachingDetail: String? = null,
)

/**
 * ACADEMY, BELT_WEIGHT, POSITION_BEST, POSITION_FAVORITE, SUBMISSION_BEST, SUBMISSION_FAVORITE, TECHNIQUE_BEST, TECHNIQUE_FAVORITE, COMPETITION, OWNER_INFO
 * **/
sealed class PROFILE_REQUEST_TYPE(open val name: String) {
    data class ACADEMY(override val name: String = "ACADEMY") : PROFILE_REQUEST_TYPE(name)  // 도장정보 도장명
    data class BELT_WEIGHT(override val name: String = "BELT_WEIGHT") : PROFILE_REQUEST_TYPE(name) // 벨트 성별 체급
    data class POSITION_BEST(override val name: String = "POSITION_BEST"): PROFILE_REQUEST_TYPE(name) // Best 포지션
    data class POSITION_FAVORITE(override val name: String = "POSITION_FAVORITE"): PROFILE_REQUEST_TYPE(name) // Favorite 포지션
    data class TECHNIQUE_BEST(override val name: String = "TECHNIQUE_BEST"): PROFILE_REQUEST_TYPE(name) // Best 기술
    data class TECHNIQUE_FAVORITE(override val name: String = "TECHNIQUE_FAVORITE"): PROFILE_REQUEST_TYPE(name) // Favorite 기술
    data class SUBMISSION_BEST(override val name: String = "SUBMISSION_BEST"): PROFILE_REQUEST_TYPE(name) // Best 서브미션
    data class SUBMISSION_FAVORITE(override val name: String = "SUBMISSION_FAVORITE"): PROFILE_REQUEST_TYPE(name) // Favorite 서브미션

    data class COMPETITION(override val name: String = "COMPETITION"): PROFILE_REQUEST_TYPE(name) // 대회정보
    data class OWNER_INFO(override val name: String = "OWNER_INFO"): PROFILE_REQUEST_TYPE(name) // 관장 사범 정보
}

