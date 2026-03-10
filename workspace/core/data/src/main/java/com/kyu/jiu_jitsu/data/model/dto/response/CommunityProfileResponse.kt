package com.kyu.jiu_jitsu.data.model.dto.response

import com.kyu.jiu_jitsu.data.model.CommunityProfileInfo
import com.kyu.jiu_jitsu.data.model.CompetitionInfo
import com.kyu.jiu_jitsu.data.model.toBeltRank
import com.kyu.jiu_jitsu.data.model.toBeltStripe
import com.kyu.jiu_jitsu.data.model.toCompetitionRank
import com.kyu.jiu_jitsu.data.model.toGender
import com.kyu.jiu_jitsu.data.model.toPosition
import com.kyu.jiu_jitsu.data.model.toSubmission
import com.kyu.jiu_jitsu.data.model.toTechnique
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CommunityProfileResponse(
    val success: Boolean?,
    val code: String?,
    val message: String?,
    val data: CommunityProfileData?
)

@JsonClass(generateAdapter = true)
data class CommunityProfileData(
    val nickname: String? = null,
    val profileImageUrl: String? = null,
    val beltRank: String? = null,
    val beltStripe: String? = null,
    val gender: String? = null,
    val weightKg: Double? = null,
    val academyName: String? = null,
    val competitionInfoList: List<Competition>? = null,
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

@JsonClass(generateAdapter = true)
data class Competition(
    var competitionYear: Int? = null,
    var competitionMonth: Int? = null,
    var competitionName: String? = null,
    var competitionRank: String? = null,
)

fun CommunityProfileData?.toInfo(): CommunityProfileInfo {
    return if (this == null) {
        CommunityProfileInfo()
    } else {
        CommunityProfileInfo(
            nickname = nickname ?:"",
            profileImageUrl = profileImageUrl ?: "",
            beltRank = beltRank.toBeltRank(),
            beltStripe = beltStripe?.toBeltStripe(),
            gender = gender?.toGender(),
            weightKg = weightKg,
            academyName = academyName ?: "",
            competitions = competitionInfoList.toInfo(),
            bestSubmission = bestSubmission.toSubmission(),
            favoriteSubmission = favoriteSubmission.toSubmission(),
            bestTechnique = bestTechnique.toTechnique(),
            favoriteTechnique = favoriteTechnique.toTechnique(),
            bestPosition = bestPosition.toPosition(),
            favoritePosition = favoritePosition.toPosition(),
            isWeightHidden = isWeightHidden ?: false,
        )
    }
}

fun List<Competition>?.toInfo(): List<CompetitionInfo>? {
    return if (this == null) {
        null
    } else {
        this.map { it.toInfo() }
    }
}

fun Competition?.toInfo(): CompetitionInfo {
    return if (this == null) {
        CompetitionInfo()
    } else {
        CompetitionInfo(
            competitionYear = competitionYear,
            competitionMonth = competitionMonth,
            competitionName = competitionName,
            competitionRank = competitionRank.toCompetitionRank(),
        )
    }
}