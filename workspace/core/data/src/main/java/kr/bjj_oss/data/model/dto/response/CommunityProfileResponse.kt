package kr.bjj_oss.data.model.dto.response

import kr.bjj_oss.model.CommunityProfileInfo
import kr.bjj_oss.model.CompetitionInfo
import kr.bjj_oss.model.toBeltRank
import kr.bjj_oss.model.toBeltStripe
import kr.bjj_oss.model.toCompetitionRank
import kr.bjj_oss.model.toGender
import kr.bjj_oss.model.toPosition
import kr.bjj_oss.model.toSubmission
import kr.bjj_oss.model.toTechnique
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CommunityProfileResponse(
    override val success: Boolean?,
    override val code: String?,
    override val message: String?,
    override val data: CommunityProfileData?
) : ApiResponse<CommunityProfileData>

@JsonClass(generateAdapter = true)
data class CommunityProfileData(
    val nickname: String? = null,
    val profileImageUrl: String? = null,
    val profileImage: CommunityProfileImage? = null,
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
data class CommunityProfileImage(
    val id: Int? = null,
    val imageUrl: String? = null,
)

@JsonClass(generateAdapter = true)
data class Competition(
    val competitionYear: Int? = null,
    val competitionMonth: Int? = null,
    val competitionName: String? = null,
    val competitionRank: String? = null,
)

/** The data-layer boundary where backend strings become typed application profile values. */
internal fun CommunityProfileData.toInfo(): CommunityProfileInfo = CommunityProfileInfo(
    nickname = nickname.orEmpty(),
    profileImageUrl = profileImage?.imageUrl ?: profileImageUrl.orEmpty(),
    beltRank = beltRank.toBeltRank(),
    beltStripe = beltStripe.toBeltStripe(),
    gender = gender.toGender(),
    weightKg = weightKg,
    academyName = academyName.orEmpty(),
    competitions = competitionInfoList?.map(Competition::toInfo),
    bestSubmission = bestSubmission.toSubmission(),
    favoriteSubmission = favoriteSubmission.toSubmission(),
    bestTechnique = bestTechnique.toTechnique(),
    favoriteTechnique = favoriteTechnique.toTechnique(),
    bestPosition = bestPosition.toPosition(),
    favoritePosition = favoritePosition.toPosition(),
    isWeightHidden = isWeightHidden ?: false,
)

internal fun Competition.toInfo(): CompetitionInfo = CompetitionInfo(
    competitionYear = competitionYear,
    competitionMonth = competitionMonth,
    competitionName = competitionName,
    competitionRank = competitionRank.toCompetitionRank(),
)
