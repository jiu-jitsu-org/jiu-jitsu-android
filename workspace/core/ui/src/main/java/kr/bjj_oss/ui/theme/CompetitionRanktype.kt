package kr.bjj_oss.ui.theme

import kr.bjj_oss.model.COMPETITION_RANK
import kr.bjj_oss.ui.R

fun COMPETITION_RANK.getIconDrawableRes(): Int {
    return when(this) {
        is COMPETITION_RANK.GOLD -> R.drawable.ic_profile_default
        is COMPETITION_RANK.SILVER -> R.drawable.ic_profile_default
        is COMPETITION_RANK.BRONZE -> R.drawable.ic_profile_default
        is COMPETITION_RANK.PARTICIPATION -> R.drawable.ic_profile_default
    }
}
