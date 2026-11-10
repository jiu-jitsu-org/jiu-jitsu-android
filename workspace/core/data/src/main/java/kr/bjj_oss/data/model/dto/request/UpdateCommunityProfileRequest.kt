package kr.bjj_oss.data.model.dto.request

import kr.bjj_oss.data.model.dto.response.CompetitionGroupDto
import kr.bjj_oss.data.model.dto.response.CompetitionDivisionDto
import kr.bjj_oss.model.CommunityProfileField
import kr.bjj_oss.model.CompetitionInfo
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
    val competitions: List<CompetitionGroupDto>? = null,
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
        competitions = if (field == CommunityProfileField.COMPETITION) competitions.orEmpty().toGroups() else null,
        bestSubmission = bestSubmission?.name,
        favoriteSubmission = favoriteSubmission?.name,
        bestTechnique = bestTechnique?.name,
        favoriteTechnique = favoriteTechnique?.name,
        bestPosition = bestPosition?.name,
        favoritePosition = favoritePosition?.name,
        isWeightHidden = isWeightHidden,
        teachingPhilosophy = teachingPhilosophy,
        teachingStartDate = teachingStartDate,
        teachingDetail = teachingDetail,
    )

/** Wire grouping deliberately preserves case and internal whitespace, unlike UI grouping. */
private fun List<CompetitionInfo>.toGroups(): List<CompetitionGroupDto> {
    val groups = linkedMapOf<String, MutableList<CompetitionInfo>>()
    forEachIndexed { index, entry ->
        val name = entry.competitionName.orEmpty().trim()
        val key = if (name.isEmpty()) "empty:$index" else "${entry.competitionYear}|$name"
        groups.getOrPut(key) { mutableListOf() }.add(entry)
    }
    val usedIds = mutableSetOf<Int>()
    return groups.values.map { entries ->
        val first = entries.first()
        CompetitionGroupDto(
            competitionYear = first.competitionYear,
            competitionName = first.competitionName.orEmpty().trim(),
            competitionPinned = entries.any { it.isPinned },
            competitionDisplayOrder = entries.mapNotNull { it.displayOrder }.minOrNull(),
            competitionId = entries.firstNotNullOfOrNull { it.competitionId }?.takeIf { usedIds.add(it) },
            divisions = entries.map { entry ->
                CompetitionDivisionDto(
                    competitionRank = entry.competitionRank?.name ?: "PARTICIPATION",
                    competitionUniform = entry.uniform?.name,
                    competitionBelt = entry.belt?.name,
                    competitionWeightLimit = entry.weightLimit,
                    competitionWeightBound = entry.weightBound?.name,
                    competitionAbsolute = entry.absolute,
                )
            },
        )
    }
}
