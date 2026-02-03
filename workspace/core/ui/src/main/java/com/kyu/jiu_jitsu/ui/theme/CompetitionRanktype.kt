package com.kyu.jiu_jitsu.ui.theme

import com.kyu.jiu_jitsu.data.model.COMPETITION_RANK
import com.kyu.jiu_jitsu.ui.R

fun COMPETITION_RANK.getIconDrawableRes(): Int {
    return when(this) {
        is COMPETITION_RANK.GOLD -> R.drawable.ic_profile_default
    }
}