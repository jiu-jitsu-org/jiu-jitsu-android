package kr.bjj_oss.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kr.bjj_oss.model.*
import kr.bjj_oss.profile.R

@Composable
fun beltLabel(belt: BELT_RANK?): String = when (belt) {
    is BELT_RANK.WHITE -> stringResource(R.string.profile_belt_white)
    is BELT_RANK.BLUE -> stringResource(R.string.profile_belt_blue)
    is BELT_RANK.PURPLE -> stringResource(R.string.profile_belt_purple)
    is BELT_RANK.BROWN -> stringResource(R.string.profile_belt_brown)
    is BELT_RANK.BLACK -> stringResource(R.string.profile_belt_black)
    else -> stringResource(R.string.profile_none)
}

@Composable
fun stripeLabel(stripe: BELT_STRIPE?): String =
    if (stripe == null) "" else if (stripe is BELT_STRIPE.STRIPE_0) stringResource(R.string.profile_stripe_zero)
    else stringResource(R.string.profile_stripe_count, BELT_STRIPE_LIST.indexOf(stripe))

@Composable
fun medalLabel(rank: COMPETITION_RANK?): String = stringResource(when (rank) {
    is COMPETITION_RANK.GOLD -> R.string.profile_gold
    is COMPETITION_RANK.SILVER -> R.string.profile_silver
    is COMPETITION_RANK.BRONZE -> R.string.profile_bronze
    else -> R.string.profile_participation
})

@Composable
fun divisionLabel(entry: CompetitionInfo, includeBelt: Boolean = true): String {
    val parts = mutableListOf<String>()
    if (includeBelt && entry.belt != null) parts += beltLabel(entry.belt)
    entry.uniform?.let { parts += stringResource(if (it == CompetitionUniform.GI) R.string.profile_gi else R.string.profile_no_gi) }
    if (entry.absolute) parts += stringResource(R.string.profile_absolute)
    else if (entry.weightBound == CompetitionWeightBound.OVER) parts += stringResource(R.string.profile_unlimited)
    else entry.weightLimit?.let { parts += "-${if (it % 1 == 0.0) it.toInt().toString() else it.toString()}kg" }
    return parts.joinToString(" · ").ifEmpty { medalLabel(entry.competitionRank) }
}

fun styleLabelRes(wire: String?): Int = when (wire) {
    "TOP" -> R.string.profile_top; "GUARD" -> R.string.profile_guard
    "GUARD_PASSES" -> R.string.profile_guard_pass; "SWEEPS" -> R.string.profile_sweep
    "TAKE_DOWNS" -> R.string.profile_takedown; "ESCAPES" -> R.string.profile_escape
    "CHOKES" -> R.string.profile_choke; "ARM_LOCKS" -> R.string.profile_arm_lock; "LEG_LOCKS" -> R.string.profile_leg_lock
    else -> R.string.profile_unspecified
}
fun styleIcon(wire: String?): Int = when (wire) {
    "TOP" -> R.drawable.profile_icon_style_top_position; "GUARD" -> R.drawable.profile_icon_style_guard_position
    "GUARD_PASSES" -> R.drawable.profile_icon_style_guard_pass; "SWEEPS" -> R.drawable.profile_icon_style_sweep
    "TAKE_DOWNS" -> R.drawable.profile_icon_style_takedown; "ESCAPES" -> R.drawable.profile_icon_style_escape_defense
    "CHOKES" -> R.drawable.profile_icon_style_choke; "ARM_LOCKS" -> R.drawable.profile_icon_style_arm_lock
    "LEG_LOCKS" -> R.drawable.profile_icon_style_leg_lock
    else -> kr.bjj_oss.ui.R.drawable.ic_square_dashed
}
