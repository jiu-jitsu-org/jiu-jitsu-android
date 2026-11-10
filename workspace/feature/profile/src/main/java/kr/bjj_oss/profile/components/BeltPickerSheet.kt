package kr.bjj_oss.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import kr.bjj_oss.model.BELT_RANK
import kr.bjj_oss.model.BELT_RANK_LIST
import kr.bjj_oss.model.BELT_STRIPE
import kr.bjj_oss.model.BELT_STRIPE_LIST
import kr.bjj_oss.profile.R

/** Uses the legacy wheel layout with the current profile's saved belt/stripe as initial values. */
@Composable
fun BeltPickerSheet(
    rank: BELT_RANK?,
    stripe: BELT_STRIPE?,
    onConfirm: (BELT_RANK, BELT_STRIPE) -> Unit,
) {
    var rankIndex by rememberSaveable {
        mutableIntStateOf(BELT_RANK_LIST.indexOf(rank).coerceAtLeast(0))
    }
    var stripeIndex by rememberSaveable {
        mutableIntStateOf(BELT_STRIPE_LIST.indexOf(stripe).coerceAtLeast(0))
    }
    BeltRankPickerContent(
        rankIndex = rankIndex,
        stripeIndex = stripeIndex,
        confirmText = stringResource(R.string.profile_confirm),
        onRankChange = { rankIndex = BELT_RANK_LIST.indexOf(it) },
        onStripeChange = { stripeIndex = BELT_STRIPE_LIST.indexOf(it) },
        onBottomBtnClick = { onConfirm(BELT_RANK_LIST[rankIndex], BELT_STRIPE_LIST[stripeIndex]) },
    )
}
