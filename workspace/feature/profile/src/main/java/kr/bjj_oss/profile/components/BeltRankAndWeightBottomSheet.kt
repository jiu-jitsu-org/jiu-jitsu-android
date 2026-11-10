package kr.bjj_oss.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kr.bjj_oss.model.BELT_RANK
import kr.bjj_oss.model.BELT_RANK_LIST
import kr.bjj_oss.model.BELT_STRIPE
import kr.bjj_oss.model.BELT_STRIPE_LIST
import kr.bjj_oss.ui.R
import kr.bjj_oss.ui.components.button.PrimaryButton
import kr.bjj_oss.ui.components.picker.VerticalWheelPicker
import kr.bjj_oss.ui.theme.Typography
import kr.bjj_oss.ui.theme.White

@Composable
fun BeltRankAndWeightBottomSheet(
    onFinished:(
            beltRank: BELT_RANK,
            beltStripe: BELT_STRIPE,
    ) -> Unit,
) {
    var beltRank by remember { mutableStateOf(BELT_RANK_LIST[0]) }
    var beltStripe by remember { mutableStateOf(BELT_STRIPE_LIST[0]) }

    BeltRankPickerContent(
        rankIndex = BELT_RANK_LIST.indexOf(beltRank).coerceAtLeast(0),
        stripeIndex = BELT_STRIPE_LIST.indexOf(beltStripe).coerceAtLeast(0),
        onRankChange = { selectedRank -> beltRank = selectedRank },
        onStripeChange = { selectedStripe -> beltStripe = selectedStripe },
        confirmText = stringResource(kr.bjj_oss.profile.R.string.profile_pass_belt),
        onBottomBtnClick = {
            onFinished(beltRank, beltStripe)
        }
    )
}

/** 벨트 정보 입력 */
@Composable
internal fun BeltRankPickerContent(
    rankIndex: Int,
    stripeIndex: Int,
    confirmText: String,
    onRankChange: (BELT_RANK) -> Unit = {},
    onStripeChange: (BELT_STRIPE) -> Unit = {},
    onBottomBtnClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(rememberScrollState())
            .background(color = White)
            .padding(
                horizontal = 20.dp,
                vertical = 15.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Title
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.common_belt),
            style = Typography.titleMedium,
            textAlign = TextAlign.Start,
        )
        Spacer(modifier = Modifier.height(16.dp))

        BeltRankWheel(
            rankValue = rankIndex,
            onRankChange = onRankChange,
            stripeValue = stripeIndex,
            onStripeChange = onStripeChange,
            beltRankList = BELT_RANK_LIST,
            beltStripeList = BELT_STRIPE_LIST,
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
            text = confirmText,
            onClick = onBottomBtnClick
        )

    }
}

@Composable
private fun BeltRankWheel(
    rankValue: Int,
    onRankChange: (BELT_RANK) -> Unit,
    stripeValue: Int,
    onStripeChange: (BELT_STRIPE) -> Unit,
    beltRankList: List<BELT_RANK>,
    beltStripeList: List<BELT_STRIPE>,
) {
    val fontScale = LocalDensity.current.fontScale.coerceAtLeast(1f)
    val rankLabels = beltRankList.associate { it.name to beltLabel(it) }
    val stripeLabels = beltStripeList.associate { it.name to stripeLabel(it) }
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val wheelWidth = minOf(86.dp * fontScale, ((maxWidth - 8.dp) / 2).coerceAtLeast(1.dp))
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            VerticalWheelPicker(
                items = beltRankList,
                initialIndex = rankValue,
                onSelected = { _, rank -> onRankChange(rank) },
                itemHeight = 44.dp * fontScale,
                itemWidth = wheelWidth,
                itemText = { rank -> rankLabels.getValue(rank.name) },
            )
            Spacer(modifier = Modifier.width(8.dp))
            VerticalWheelPicker(
                items = beltStripeList,
                initialIndex = stripeValue,
                onSelected = { _, stripe -> onStripeChange(stripe) },
                itemHeight = 44.dp * fontScale,
                itemWidth = wheelWidth,
                itemText = { stripe -> stripeLabels.getValue(stripe.name) },
            )
        }
    }
}
