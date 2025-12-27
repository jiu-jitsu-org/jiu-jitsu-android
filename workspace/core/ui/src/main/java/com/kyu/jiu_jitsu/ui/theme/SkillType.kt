package com.kyu.jiu_jitsu.ui.theme

import com.kyu.jiu_jitsu.data.model.POSITION
import com.kyu.jiu_jitsu.data.model.SUBMISSION
import com.kyu.jiu_jitsu.data.model.TECHNIQUE
import com.kyu.jiu_jitsu.ui.R

/**
 * 각 포지션 아이콘 반환
 */
fun POSITION.getDrawableRes(): Int {
    return when(this) {
        is POSITION.TOP -> R.drawable.ic_position_top
        is POSITION.GUARD -> R.drawable.ic_position_guard
    }
}

/**
 * 각 포지션 카드 배경 반환
 */
fun POSITION.getBgDrawableRes(): Int {
    return when(this) {
        is POSITION.TOP -> R.drawable.bg_position_top
        is POSITION.GUARD -> R.drawable.bg_position_guard
    }
}

/**
 * 각 기술 아이콘 반환
 */
fun TECHNIQUE.getDrawableRes(): Int {
    return when(this) {
        is TECHNIQUE.SWEEPS -> R.drawable.ic_technique_sweeps
        is TECHNIQUE.GUARD_PASSES -> R.drawable.ic_technique_guard_passes
        is TECHNIQUE.TAKE_DOWNS -> R.drawable.ic_profile_default
        is TECHNIQUE.ESCAPES -> R.drawable.ic_technique_escapes
    }
}

/**
 * 각 서브미션 아이콘 반환
 */
fun SUBMISSION.getDrawableRes(): Int {
    return when(this) {
        is SUBMISSION.CHOKES -> R.drawable.ic_submission_chokes
        is SUBMISSION.ARM_LOCKS -> R.drawable.ic_submission_arm_locks
        is SUBMISSION.LEG_LOCKS -> R.drawable.ic_submission_leg_locks
    }
}