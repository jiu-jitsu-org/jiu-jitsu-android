package com.kyu.jiu_jitsu.model

/**
 * Belt rank known by the app.
 *
 * [name] is the stable backend wire value. [displayName] is retained during the current migration
 * because existing screens use it outside Composables; future localization can move display labels
 * to UI resources without changing the wire value.
 */
sealed class BELT_RANK(
    open val name: String,
    open val displayName: String = "",
) {
    data class WHITE(
        override val name: String = "WHITE",
        override val displayName: String = "화이트",
    ) : BELT_RANK(name, displayName)

    data class BLUE(
        override val name: String = "BLUE",
        override val displayName: String = "블루",
    ) : BELT_RANK(name, displayName)

    data class PURPLE(
        override val name: String = "PURPLE",
        override val displayName: String = "퍼플",
    ) : BELT_RANK(name, displayName)

    data class BROWN(
        override val name: String = "BROWN",
        override val displayName: String = "브라운",
    ) : BELT_RANK(name, displayName)

    data class BLACK(
        override val name: String = "BLACK",
        override val displayName: String = "블랙",
    ) : BELT_RANK(name, displayName)
}

/** Canonical belt ordering used by profile selectors. */
val BELT_RANK_LIST = listOf(
    BELT_RANK.WHITE(),
    BELT_RANK.BLUE(),
    BELT_RANK.PURPLE(),
    BELT_RANK.BROWN(),
    BELT_RANK.BLACK(),
)

/** Belt stripe/gral value stored by the backend. */
sealed class BELT_STRIPE(
    open val name: String,
    open val displayName: String = "",
) {
    data class STRIPE_0(
        override val name: String = "STRIPE_0",
        override val displayName: String = "무그랄",
    ) : BELT_STRIPE(name, displayName)

    data class STRIPE_1(
        override val name: String = "STRIPE_1",
        override val displayName: String = "1그랄",
    ) : BELT_STRIPE(name, displayName)

    data class STRIPE_2(
        override val name: String = "STRIPE_2",
        override val displayName: String = "2그랄",
    ) : BELT_STRIPE(name, displayName)

    data class STRIPE_3(
        override val name: String = "STRIPE_3",
        override val displayName: String = "3그랄",
    ) : BELT_STRIPE(name, displayName)

    data class STRIPE_4(
        override val name: String = "STRIPE_4",
        override val displayName: String = "4그랄",
    ) : BELT_STRIPE(name, displayName)
}

/** Canonical stripe ordering used by profile selectors. */
val BELT_STRIPE_LIST = listOf(
    BELT_STRIPE.STRIPE_0(),
    BELT_STRIPE.STRIPE_1(),
    BELT_STRIPE.STRIPE_2(),
    BELT_STRIPE.STRIPE_3(),
    BELT_STRIPE.STRIPE_4(),
)

/** Gender values currently supported by the backend profile contract. */
sealed class GENDER(
    open val name: String,
    open val displayName: String = "",
) {
    data class FEMALE(
        override val name: String = "FEMALE",
        override val displayName: String = "여자",
    ) : GENDER(name, displayName)

    data class MALE(
        override val name: String = "MALE",
        override val displayName: String = "남자",
    ) : GENDER(name, displayName)
}

/** Canonical gender ordering used by profile selectors. */
val GENDER_LIST = listOf(GENDER.FEMALE(), GENDER.MALE())

/** Submission family selected as a user's best or favorite skill. */
sealed class SUBMISSION(
    open val name: String,
    open val displayName: String = "",
    open val cardInfo: String = "",
) {
    data class CHOKES(
        override val name: String = "CHOKES",
        override val displayName: String = "조르기",
        override val cardInfo: String = "내 몸은 아나콘다, 넌 그냥 먹잇감. 탭이 늦으면, 네 뼈가 먼저 비명을 지를 거다.",
    ) : SUBMISSION(name, displayName, cardInfo)

    data class ARM_LOCKS(
        override val name: String = "ARM_LOCKS",
        override val displayName: String = "팔 관절기",
        override val cardInfo: String = "도망갈 곳은 없어. 네 팔은 내 손아귀에 완벽히 잡혔으니까. 남은 건 네 결정뿐.",
    ) : SUBMISSION(name, displayName, cardInfo)

    data class LEG_LOCKS(
        override val name: String = "LEG_LOCKS",
        override val displayName: String = "하체 관절기",
        override val cardInfo: String = "네가 아무리 발버둥 쳐도 소용없어. 네 하체는 이제 완벽하게 내 통제하에 들어왔으니까.",
    ) : SUBMISSION(name, displayName, cardInfo)
}

/** Technique family selected as a user's best or favorite skill. */
sealed class TECHNIQUE(
    open val name: String,
    open val displayName: String = "",
    open val cardInfo: String = "",
) {
    data class SWEEPS(
        override val name: String = "SWEEPS",
        override val displayName: String = "스윕·뒤집기",
        override val cardInfo: String = "지금 네가 보는 천장, 곧 네 등이 마주할 매트가 될 거다. 세상이 뒤집히는 기분을 즐겨보라고.",
    ) : TECHNIQUE(name, displayName, cardInfo)

    data class GUARD_PASSES(
        override val name: String = "GUARD_PASSES",
        override val displayName: String = "가드패스",
        override val cardInfo: String = "계속 막아봐. 네 다리는 고작 두 개뿐이지만, 내가 뚫을 패스는 무한하거든.",
    ) : TECHNIQUE(name, displayName, cardInfo)

    data class TAKE_DOWNS(
        override val name: String = "TAKE_DOWNS",
        override val displayName: String = "테이크다운",
        override val cardInfo: String = "이 매트 위에서 '선다'는 건 없어. '아직 넘어지지 않았다'만 있을 뿐. 그리고 그 시간은, 이제 끝났어.",
    ) : TECHNIQUE(name, displayName, cardInfo)

    data class ESCAPES(
        override val name: String = "ESCAPES",
        override val displayName: String = "이스케이프\n디펜스",
        override val cardInfo: String = "계속 그렇게 힘을 낭비해. 네가 헛된 그림자에 매달려 있을 때, 난 이미 사라지고 없을 걸?",
    ) : TECHNIQUE(name, displayName, cardInfo)
}

/** Positional preference selected as a user's best or favorite style. */
sealed class POSITION(
    open val name: String,
    open val displayName: String = "",
    open val cardInfo: String = "",
) {
    data class TOP(
        override val name: String = "TOP",
        override val displayName: String = "탑 포지션",
        override val cardInfo: String = "이 매트 위에서 '선다'는 건 없어. '아직 넘어지지 않았다'만 있을 뿐. 그리고 그 시간은, 이제 끝났어.",
    ) : POSITION(name, displayName, cardInfo)

    data class GUARD(
        override val name: String = "GUARD",
        override val displayName: String = "가드 포지션",
        override val cardInfo: String = "이 매트 위에서 '선다'는 건 없어. '아직 넘어지지 않았다'만 있을 뿐. 그리고 그 시간은, 이제 끝났어.",
    ) : POSITION(name, displayName, cardInfo)
}

/** Competition outcome stored in community-profile history. */
sealed class COMPETITION_RANK(
    open val name: String,
    open val displayName: String = "",
) {
    data class GOLD(
        override val name: String = "GOLD",
        override val displayName: String = "금메달",
    ) : COMPETITION_RANK(name, displayName)

    data class SILVER(
        override val name: String = "SILVER",
        override val displayName: String = "은메달",
    ) : COMPETITION_RANK(name, displayName)

    data class BRONZE(
        override val name: String = "BRONZE",
        override val displayName: String = "동메달",
    ) : COMPETITION_RANK(name, displayName)

    data class PARTICIPATION(
        override val name: String = "PARTICIPATION",
        override val displayName: String = "참가",
    ) : COMPETITION_RANK(name, displayName)
}

/** Converts a nullable backend wire value to an app belt rank. */
fun String?.toBeltRank(): BELT_RANK? = when (this) {
    "WHITE" -> BELT_RANK.WHITE()
    "BLUE" -> BELT_RANK.BLUE()
    "PURPLE" -> BELT_RANK.PURPLE()
    "BROWN" -> BELT_RANK.BROWN()
    "BLACK" -> BELT_RANK.BLACK()
    else -> null
}

/** Converts a nullable backend wire value to an app stripe value. */
fun String?.toBeltStripe(): BELT_STRIPE? = when (this) {
    "STRIPE_0" -> BELT_STRIPE.STRIPE_0()
    "STRIPE_1" -> BELT_STRIPE.STRIPE_1()
    "STRIPE_2" -> BELT_STRIPE.STRIPE_2()
    "STRIPE_3" -> BELT_STRIPE.STRIPE_3()
    "STRIPE_4" -> BELT_STRIPE.STRIPE_4()
    else -> null
}

/** Converts a nullable backend wire value to an app gender value. */
fun String?.toGender(): GENDER? = when (this) {
    "FEMALE" -> GENDER.FEMALE()
    "MALE" -> GENDER.MALE()
    else -> null
}

/** Converts a nullable backend wire value to a submission family. */
fun String?.toSubmission(): SUBMISSION? = when (this) {
    "CHOKES" -> SUBMISSION.CHOKES()
    "ARM_LOCKS" -> SUBMISSION.ARM_LOCKS()
    "LEG_LOCKS" -> SUBMISSION.LEG_LOCKS()
    else -> null
}

/** Converts a nullable backend wire value to a technique family. */
fun String?.toTechnique(): TECHNIQUE? = when (this) {
    "SWEEPS" -> TECHNIQUE.SWEEPS()
    "GUARD_PASSES" -> TECHNIQUE.GUARD_PASSES()
    "TAKE_DOWNS" -> TECHNIQUE.TAKE_DOWNS()
    "ESCAPES" -> TECHNIQUE.ESCAPES()
    else -> null
}

/** Converts a nullable backend wire value to a positional preference. */
fun String?.toPosition(): POSITION? = when (this) {
    "TOP" -> POSITION.TOP()
    "GUARD" -> POSITION.GUARD()
    else -> null
}

/** Converts every competition value supported by the current backend contract. */
fun String?.toCompetitionRank(): COMPETITION_RANK? = when (this) {
    "GOLD" -> COMPETITION_RANK.GOLD()
    "SILVER" -> COMPETITION_RANK.SILVER()
    "BRONZE" -> COMPETITION_RANK.BRONZE()
    "PARTICIPATION" -> COMPETITION_RANK.PARTICIPATION()
    else -> null
}

/** Immutable community-profile snapshot consumed by profile UI. */
data class CommunityProfileInfo(
    val nickname: String = "",
    val profileImageUrl: String? = null,
    val beltRank: BELT_RANK? = null,
    val beltStripe: BELT_STRIPE? = null,
    val gender: GENDER? = null,
    val weightKg: Double? = null,
    val isWeightHidden: Boolean? = null,
    val academyName: String = "",
    val competitions: List<CompetitionInfo>? = null,
    val bestSubmission: SUBMISSION? = null,
    val favoriteSubmission: SUBMISSION? = null,
    val bestTechnique: TECHNIQUE? = null,
    val favoriteTechnique: TECHNIQUE? = null,
    val bestPosition: POSITION? = null,
    val favoritePosition: POSITION? = null,
)

/** Immutable competition entry within a community profile. */
data class CompetitionInfo(
    val competitionYear: Int? = null,
    val competitionMonth: Int? = null,
    val competitionName: String? = null,
    val competitionRank: COMPETITION_RANK? = null,
)

/** Returns true when the optional Jiu-Jitsu profile sections contain no data. */
fun CommunityProfileInfo?.isDataEmpty(): Boolean =
    this == null || (
        beltRank == null &&
            beltStripe == null &&
            academyName.isEmpty() &&
            competitions.isNullOrEmpty() &&
            bestSubmission == null &&
            favoriteSubmission == null &&
            bestTechnique == null &&
            favoriteTechnique == null &&
            bestPosition == null &&
            favoritePosition == null
        )

/** Returns true when at least one optional profile section can be modified. */
fun CommunityProfileInfo?.isShowModify(): Boolean = !isDataEmpty()

/**
 * Identifies the partial community-profile operation expected by the backend.
 *
 * The wire value is centralized here so feature code does not depend on request DTO definitions.
 */
enum class CommunityProfileField(val wireValue: String) {
    ACADEMY("ACADEMY"),
    BELT_WEIGHT("BELT_WEIGHT"),
    POSITION_BEST("POSITION_BEST"),
    POSITION_FAVORITE("POSITION_FAVORITE"),
    TECHNIQUE_BEST("TECHNIQUE_BEST"),
    TECHNIQUE_FAVORITE("TECHNIQUE_FAVORITE"),
    SUBMISSION_BEST("SUBMISSION_BEST"),
    SUBMISSION_FAVORITE("SUBMISSION_FAVORITE"),
    COMPETITION("COMPETITION"),
    OWNER_INFO("OWNER_INFO"),
}

/**
 * App-facing command for a partial community-profile update.
 *
 * Nullable fields mean “not part of this partial update.” The data layer converts this command to
 * the backend DTO, keeping JSON and Moshi details out of feature ViewModels.
 */
data class CommunityProfileUpdate(
    val field: CommunityProfileField,
    val nickname: String? = null,
    val profileImageUrl: String? = null,
    val beltRank: BELT_RANK? = null,
    val beltStripe: BELT_STRIPE? = null,
    val gender: GENDER? = null,
    val weightKg: Double? = null,
    val academyName: String? = null,
    val competitions: List<CompetitionInfo>? = null,
    val bestSubmission: SUBMISSION? = null,
    val favoriteSubmission: SUBMISSION? = null,
    val bestTechnique: TECHNIQUE? = null,
    val favoriteTechnique: TECHNIQUE? = null,
    val bestPosition: POSITION? = null,
    val favoritePosition: POSITION? = null,
    val isWeightHidden: Boolean? = null,
)
