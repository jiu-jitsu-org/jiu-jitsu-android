package com.kyu.jiu_jitsu.data.model

sealed class BELT_RANK(
    open val name: String,
    open val displayName: String = "",
) {
    data class WHITE(
        override val name: String = "WHITE",
        override val displayName: String = "화이트",
    ) : BELT_RANK(name)

    data class BLUE(
        override val name: String = "BLUE",
        override val displayName: String = "블루",
    ) : BELT_RANK(name)

    data class PURPLE(
        override val name: String = "PURPLE",
        override val displayName: String = "퍼플",
    ) : BELT_RANK(name)

    data class BROWN(
        override val name: String = "BROWN",
        override val displayName: String = "브라운",
    ) : BELT_RANK(name)

    data class BLACK(
        override val name: String = "BLACK",
        override val displayName: String = "블랙",
    ) : BELT_RANK(name)
}

val BELT_RANK_LIST = listOf(
    BELT_RANK.WHITE(),
    BELT_RANK.BLUE(),
    BELT_RANK.PURPLE(),
    BELT_RANK.BROWN(),
    BELT_RANK.BLACK(),
)

sealed class BELT_STRIPE(
    open val name: String,
    open val displayName: String = "",
) {

    data class STRIPE_0(
        override val name: String = "STRIPE_0",
        override val displayName: String = "무그랄",
    ) : BELT_STRIPE(name)

    data class STRIPE_1(
        override val name: String = "STRIPE_1",
        override val displayName: String = "1그랄",
    ) : BELT_STRIPE(name)

    data class STRIPE_2(
        override val name: String = "STRIPE_2",
        override val displayName: String = "2그랄",
    ) : BELT_STRIPE(name)

    data class STRIPE_3(
        override val name: String = "STRIPE_3",
        override val displayName: String = "3그랄",
    ) : BELT_STRIPE(name)

    data class STRIPE_4(
        override val name: String = "STRIPE_4",
        override val displayName: String = "4그랄",
    ) : BELT_STRIPE(name)
}

val BELT_STRIPE_LIST = listOf(
    BELT_STRIPE.STRIPE_0(),
    BELT_STRIPE.STRIPE_1(),
    BELT_STRIPE.STRIPE_2(),
    BELT_STRIPE.STRIPE_3(),
    BELT_STRIPE.STRIPE_4(),
)

sealed class GENDER(
    open val name: String,
    open val displayName: String = "",
) {
    data class FEMALE(
        override val name: String = "FEMALE",
        override val displayName: String = "여자",
    ) : GENDER(name)

    data class MALE(
        override val name: String = "MALE",
        override val displayName: String = "남자",
    ) : GENDER(name)
}

val GENDER_LIST = listOf(
    GENDER.FEMALE(),
    GENDER.MALE(),
)

sealed class SUBMISSION(
    open val name: String,
    open val displayName: String = "",
) {
    data class CHOKES(
        override val name: String = "CHOKES",
        override val displayName: String = "조르기",
    ) : SUBMISSION(name, displayName)

    data class ARM_LOCKS(
        override val name: String = "ARM_LOCKS",
        override val displayName: String = "팔 관절기",
    ) : SUBMISSION(name, displayName)

    data class LEG_LOCKS(
        override val name: String = "LEG_LOCKS",
        override val displayName: String = "팔 관절기",
    ): SUBMISSION(name, displayName)
}

sealed class TECHNIQUE(
    open val name: String,
    open val displayName: String = "",
) {
    data class SWEEPS(
        override val name: String = "SWEEPS",
        override val displayName: String = "스윕·뒤집기"
    ) : TECHNIQUE(name, displayName)

    data class GUARD_PASSES(
        override val name: String = "GUARD_PASSES",
        override val displayName: String = "가드패스"
    ) : TECHNIQUE(name, displayName)

    data class TAKE_DOWNS(
        override val name: String = "TAKE_DOWNS",
        override val displayName: String = "테이크다운"
    ): TECHNIQUE(name, displayName)

    data class ESCAPES(
        override val name: String = "ESCAPES",
        override val displayName: String = "이스케이프\n디펜스"
    ): TECHNIQUE(name, displayName)
}

sealed class POSITION(
    open val name: String,
    open val displayName: String = "",
) {
    data class TOP(
        override val name: String = "TOP",
        override val displayName: String = "탑 포지션",
    ) : POSITION(name, displayName)

    data class GUARD(
        override val name: String = "GUARD",
        override val displayName: String = "가드 포지션",
    ) : POSITION(name, displayName)
}

sealed class COMPETITION_RANK(
    open val name: String,
    open val displayName: String = "",
) {
    data class GOLD(
        override val name: String = "GOLD",
        override val displayName: String = "금메달",
    ): COMPETITION_RANK(name, displayName)
}

fun String?.toBeltRank(): BELT_RANK? {
    var returnValue: BELT_RANK? = null

    returnValue = when (this) {
        "WHITE" -> BELT_RANK.WHITE()
        "BLUE" -> BELT_RANK.BLUE()
        "PURPLE" -> BELT_RANK.PURPLE()
        "BROWN" -> BELT_RANK.BROWN()
        "BLACK" -> BELT_RANK.BLACK()
        else -> null
    }
    return returnValue
}

fun String?.toBeltStripe(): BELT_STRIPE? {
    var returnValue: BELT_STRIPE? = null

    returnValue = when (this) {
        "STRIPE_1" -> BELT_STRIPE.STRIPE_1()
        "STRIPE_2" -> BELT_STRIPE.STRIPE_2()
        "STRIPE_3" -> BELT_STRIPE.STRIPE_3()
        "STRIPE_4" -> BELT_STRIPE.STRIPE_4()
        else -> null
    }
    return returnValue
}

fun String?.toGender(): GENDER? {
    var returnValue: GENDER? = null

    returnValue = when (this) {
        "FEMALE" -> GENDER.FEMALE()
        "MALE" -> GENDER.MALE()
        else -> null
    }
    return returnValue
}

fun String?.toSubmission(): SUBMISSION? {
    var returnValue: SUBMISSION? = null

    returnValue = when (this) {
        "CHOKES" -> SUBMISSION.CHOKES()
        "ARM_LOCKS" -> SUBMISSION.ARM_LOCKS()
        else -> null
    }
    return returnValue
}

fun String?.toTechnique(): TECHNIQUE? {
    var returnValue: TECHNIQUE? = null

    returnValue = when (this) {
        "SWEEPS" -> TECHNIQUE.SWEEPS()
        "GUARD_PASSES" -> TECHNIQUE.GUARD_PASSES()
        else -> null
    }
    return returnValue
}

fun String?.toPosition(): POSITION? {
    var returnValue: POSITION? = null

    returnValue = when (this) {
        "TOP" -> POSITION.TOP()
        "GUARD" -> POSITION.GUARD()
        else -> null
    }
    return returnValue
}

fun String?.toCompetitionRank(): COMPETITION_RANK? {
    var returnValue: COMPETITION_RANK? = null

    returnValue = when (this) {
        "GOLD" -> COMPETITION_RANK.GOLD()
        else -> null
    }
    return returnValue
}


data class CommunityProfileInfo(
    var nickname: String = "",
    var profileImageUrl: String? = null,
    var beltRank: BELT_RANK? = null,
    var beltStripe: BELT_STRIPE? = null,
    var gender: GENDER? = null,
    var weightKg: Double? = null,
    var isWeightHidden: Boolean? = null,
    var academyName: String = "",
    var competitions: List<CompetitionInfo>? = null,
    var bestSubmission: SUBMISSION? = null,
    var favoriteSubmission: SUBMISSION? = null,
    var bestTechnique: TECHNIQUE? = null,
    var favoriteTechnique: TECHNIQUE? = null,
    var bestPosition: POSITION? = null,
    var favoritePosition: POSITION? = null,
)

data class CompetitionInfo(
    var competitionYear: Int? = null,
    var competitionMonth: Int? = null,
    var competitionName: String? = null,
    var competitionRank: COMPETITION_RANK? = null,
)

fun CommunityProfileInfo?.isDataEmpty(): Boolean {
    return if (this == null) {
        true
    } else {
        beltRank == null && beltStripe == null && academyName.isEmpty() && competitions == null &&
                bestSubmission == null && favoriteSubmission == null && bestTechnique == null && favoriteTechnique == null &&
                bestPosition == null && favoritePosition == null
    }
}

fun CommunityProfileInfo?.isShowModify(): Boolean {
    return if (this == null) {
        false
    } else {
        beltRank != null || beltStripe != null || !academyName.isEmpty() || competitions != null ||
        bestSubmission != null || favoriteSubmission != null || bestTechnique != null || favoriteTechnique != null ||
                bestPosition != null || favoritePosition != null
    }
}

//fun CommunityProfileInfo?.isCompetitionDataEmpty(): Boolean {
//    return if (this == null) {
//        true
//    } else {
//        competitionName.isEmpty() && competitionYear == null
//    }
//}
