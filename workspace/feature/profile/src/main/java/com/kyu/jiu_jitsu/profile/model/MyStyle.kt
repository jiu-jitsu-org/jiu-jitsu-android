package com.kyu.jiu_jitsu.profile.model

import androidx.compose.ui.graphics.Color
import com.kyu.jiu_jitsu.data.model.COMPETITION_RANK
import com.kyu.jiu_jitsu.data.model.POSITION
import com.kyu.jiu_jitsu.data.model.SUBMISSION
import com.kyu.jiu_jitsu.data.model.TECHNIQUE
import com.kyu.jiu_jitsu.ui.theme.Blue500
import com.kyu.jiu_jitsu.ui.theme.CoolGray25

val POSITION_LIST = listOf(
    POSITION.TOP(),
    POSITION.GUARD(),
)

val POSITION_INDICATOR_LIST = listOf(
    StyleCardIndicator.POSITION_TOP(),
    StyleCardIndicator.POSITION_GUARD(),
)

val TECHNIQUE_LIST = listOf(
    TECHNIQUE.SWEEPS(),
    TECHNIQUE.GUARD_PASSES(),
    TECHNIQUE.TAKE_DOWNS(),
    TECHNIQUE.ESCAPES(),
)

val TECHNIQUE_INDICATOR_LIST = listOf(
    StyleCardIndicator.TECHNIQUE_SWEEPS(),
    StyleCardIndicator.TECHNIQUE_GUARD_PASSES(),
    StyleCardIndicator.TECHNIQUE_TAKE_DOWNS(),
    StyleCardIndicator.TECHNIQUE_ESCAPES(),
)

val SUBMISSION_LIST = listOf(
    SUBMISSION.CHOKES(),
    SUBMISSION.ARM_LOCKS(),
    SUBMISSION.LEG_LOCKS(),
)

val SUBMISSION_INDICATOR_LIST = listOf(
    StyleCardIndicator.SUBMISSION_CHOKES(),
    StyleCardIndicator.SUBMISSION_ARM_LOCKS(),
    StyleCardIndicator.SUBMISSION_LEG_LOCKS(),
)

val COMPETITION_RANK_LIST = listOf(
    COMPETITION_RANK.GOLD(),
    COMPETITION_RANK.SILVER(),
    COMPETITION_RANK.BRONZE(),
    COMPETITION_RANK.PARTICIPATION(),
)

sealed class StyleCardIndicator(
    open val title: String,
    open val color: Color,
) {
    // Position Card Indicator Info
    data class POSITION_TOP(
        override val title: String = "TOP",
        override val color: Color = Blue500,
    ): StyleCardIndicator(title = title, color = color)

    data class POSITION_GUARD(
        override val title: String = "GUARD",
        override val color: Color = CoolGray25,
    ): StyleCardIndicator(title = title, color = color)

    // Technique Card Indicator Info
    data class TECHNIQUE_SWEEPS(
        override val title: String = "SWEEPS",
        override val color: Color = Blue500,
    ): StyleCardIndicator(title = title, color = color)

    data class TECHNIQUE_GUARD_PASSES(
        override val title: String = "GUARD_PASSES",
        override val color: Color = CoolGray25,
    ): StyleCardIndicator(title = title, color = color)

    data class TECHNIQUE_TAKE_DOWNS(
        override val title: String = "TAKE_DOWNS",
        override val color: Color = CoolGray25,
    ): StyleCardIndicator(title = title, color = color)

    data class TECHNIQUE_ESCAPES(
        override val title: String = "ESCAPES",
        override val color: Color = CoolGray25,
    ): StyleCardIndicator(title = title, color = color)

    // Submission Card Indicator Info
    data class SUBMISSION_CHOKES(
        override val title: String = "CHOKES",
        override val color: Color = Blue500,
    ): StyleCardIndicator(title = title, color = color)

    data class SUBMISSION_ARM_LOCKS(
        override val title: String = "ARM_LOCKS",
        override val color: Color = CoolGray25,
    ): StyleCardIndicator(title = title, color = color)

    data class SUBMISSION_LEG_LOCKS(
        override val title: String = "LEG_LOCKS",
        override val color: Color = CoolGray25,
    ): StyleCardIndicator(title = title, color = color)
}
