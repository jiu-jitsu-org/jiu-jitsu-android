package kr.bjj_oss.data.model.dto.request

import kr.bjj_oss.data.model.dto.response.Competition
import kr.bjj_oss.model.CommunityProfileUpdate
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateCommunityProfileRequest(
    val profileRequestType: String,
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

/** Converts an app command to the exact JSON request expected by the backend. */
internal fun CommunityProfileUpdate.toRequest(): UpdateCommunityProfileRequest =
    UpdateCommunityProfileRequest(
        profileRequestType = field.wireValue,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        beltRank = beltRank?.name,
        beltStripe = beltStripe?.name,
        gender = gender?.name,
        weightKg = weightKg,
        academyName = academyName,
        competitionInfoList = competitions?.map { competition ->
            Competition(
                competitionYear = competition.competitionYear,
                competitionMonth = competition.competitionMonth,
                competitionName = competition.competitionName,
                competitionRank = competition.competitionRank?.name,
            )
        },
        bestSubmission = bestSubmission?.name,
        favoriteSubmission = favoriteSubmission?.name,
        bestTechnique = bestTechnique?.name,
        favoriteTechnique = favoriteTechnique?.name,
        bestPosition = bestPosition?.name,
        favoritePosition = favoritePosition?.name,
        isWeightHidden = isWeightHidden,
    )
