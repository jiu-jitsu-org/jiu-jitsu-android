package com.kyu.jiu_jitsu.ui.theme

import com.kyu.jiu_jitsu.data.model.POSITION
import com.kyu.jiu_jitsu.data.model.SUBMISSION
import com.kyu.jiu_jitsu.data.model.TECHNIQUE
import com.kyu.jiu_jitsu.ui.R

/**
 * 각 포지션 아이콘 반환
 */
fun POSITION.getIconDrawableRes(): Int {
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
fun TECHNIQUE.getIconDrawableRes(): Int {
    return when(this) {
        is TECHNIQUE.SWEEPS -> R.drawable.ic_technique_sweeps
        is TECHNIQUE.GUARD_PASSES -> R.drawable.ic_technique_guard_passes
        is TECHNIQUE.TAKE_DOWNS -> R.drawable.ic_technique_take_downs
        is TECHNIQUE.ESCAPES -> R.drawable.ic_technique_escapes
    }
}

/**
 * 각 기술 카드 배경 반환
 */
fun TECHNIQUE.getBgDrawableRes(): Int {
    return when(this) {
        is TECHNIQUE.SWEEPS -> R.drawable.bg_technique_sweeps
        is TECHNIQUE.GUARD_PASSES -> R.drawable.bg_technique_guard_passes
        is TECHNIQUE.TAKE_DOWNS -> R.drawable.bg_technique_take_downs
        is TECHNIQUE.ESCAPES -> R.drawable.bg_technique_escapes
    }
}

/**
 * 각 서브미션 아이콘 반환
 */
fun SUBMISSION.getIconDrawableRes(): Int {
    return when(this) {
        is SUBMISSION.CHOKES -> R.drawable.ic_submission_chokes
        is SUBMISSION.ARM_LOCKS -> R.drawable.ic_submission_arm_locks
        is SUBMISSION.LEG_LOCKS -> R.drawable.ic_submission_leg_locks
    }
}

/**
 * 각 서브미션 카드 배경 반환
 */
fun SUBMISSION.getBgDrawableRes(): Int {
    return when(this) {
        is SUBMISSION.CHOKES -> R.drawable.bg_submission_chokes
        is SUBMISSION.ARM_LOCKS -> R.drawable.bg_submission_arm_locks
        is SUBMISSION.LEG_LOCKS -> R.drawable.bg_submission_leg_locks
    }
}