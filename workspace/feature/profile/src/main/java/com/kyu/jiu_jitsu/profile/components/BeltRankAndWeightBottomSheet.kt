package com.kyu.jiu_jitsu.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.data.model.BELT_RANK
import com.kyu.jiu_jitsu.data.model.BELT_RANK_LIST
import com.kyu.jiu_jitsu.data.model.BELT_STRIPE
import com.kyu.jiu_jitsu.data.model.BELT_STRIPE_LIST
import com.kyu.jiu_jitsu.data.model.GENDER
import com.kyu.jiu_jitsu.data.model.GENDER_LIST
import com.kyu.jiu_jitsu.domain.combineToDouble
import com.kyu.jiu_jitsu.ui.R
import com.kyu.jiu_jitsu.ui.components.button.PrimaryButton
import com.kyu.jiu_jitsu.ui.components.picker.VerticalWheelPicker
import com.kyu.jiu_jitsu.ui.components.picker.WheelPicker
import com.kyu.jiu_jitsu.ui.components.toggle.CommonToggleButton
import com.kyu.jiu_jitsu.ui.theme.Blue500
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.CoolGray25
import com.kyu.jiu_jitsu.ui.theme.Typography
import com.kyu.jiu_jitsu.ui.theme.White

@Composable
fun BeltRankAndWeightBottomSheet(
    onFinished:(
            beltRank: BELT_RANK,
            beltStripe: BELT_STRIPE,
            gender: GENDER?,
            weightKg: Double?,
            isWeightHidden: Boolean,
    ) -> Unit,
) {

    var beltRank by remember { mutableStateOf(BELT_RANK_LIST[0]) }
    var beltStripe by remember { mutableStateOf(BELT_STRIPE_LIST[0]) }
    var gender by remember { mutableStateOf(GENDER_LIST[0]) }
    var isWeightHidden by remember { mutableStateOf(false) }

    var isStepOneFinished by remember { mutableStateOf(false) }

    var weightInt by remember { mutableIntStateOf(60) }
    var weightDec by remember { mutableIntStateOf(0) }

    if (isStepOneFinished){
        InputWeight(
            intValue = weightInt,
            onIntChange = { weightInt = it },
            decValue = weightDec,
            onDecChange = { weightDec = it },
            onGenderChange = { selectedIndex -> gender = GENDER_LIST[selectedIndex] },
            onWeightHiddenChange = { isHidden -> isWeightHidden = isHidden },
            onBottomBtnClick = {
                onFinished(beltRank, beltStripe, gender, combineToDouble(weightInt, weightDec), isWeightHidden)
            }
        )
    } else {
        InputBeltRank(
            onRankChange = { selectedIndex -> beltRank = BELT_RANK_LIST[selectedIndex] },
            onStripeChange = { selectedIndex -> beltStripe = BELT_STRIPE_LIST[selectedIndex] },
            onBottomBtnClick = { isStepOneFinished = true }
        )
    }
}

/** 벨트 정보 입력 */
@Composable
private fun InputBeltRank(
    onRankChange: (selectedIndex: Int) -> Unit = {},
    onStripeChange: (selectedIndex: Int) -> Unit = {},
    onBottomBtnClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
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
            rankValue = 0,
            onRankChange = onRankChange,
            stripeValue = 0,
            onStripeChange = onStripeChange,
            beltRankList = BELT_RANK_LIST.map { item -> item.displayName },
            beltStripeList = BELT_STRIPE_LIST.map { item -> item.displayName},
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            text = stringResource(com.kyu.jiu_jitsu.profile.R.string.profile_pass_belt),
            onClick = onBottomBtnClick
        )

    }
}

/** 체급 정보 입력 */
@Composable
private fun InputWeight(
    intValue: Int,
    onIntChange: (Int) -> Unit,
    decValue: Int,
    onDecChange: (Int) -> Unit,
    onGenderChange: (Int) -> Unit,
    onWeightHiddenChange: (Boolean) -> Unit,
    onBottomBtnClick: () -> Unit = {},
) {
    var isWeightHidden by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 15.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.common_weight),
                style = Typography.titleMedium,
                textAlign = TextAlign.Start,
            )
            Spacer(modifier = Modifier.height(5.dp))

            Row {
                Text(
                    text = stringResource(com.kyu.jiu_jitsu.profile.R.string.profile_weight_hide_toggle),
                    style = Typography.titleMedium,
                )
                CommonToggleButton(
                    isChecked = isWeightHidden,
                    onToggle = {
                        isWeightHidden = !isWeightHidden
                        onWeightHiddenChange(isWeightHidden)
                    },
                    onColor = Blue500,
                    offColor = CoolGray25,
                )
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        WeightWheel(
            intValue = intValue,
            onIntChange = onIntChange,
            decValue = decValue,
            onDecChange = onDecChange,
            onGenderChange = onGenderChange,
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            text = stringResource(R.string.common_complete),
            onClick = onBottomBtnClick
        )
    }
}

@Composable
private fun BeltRankWheel(
    rankValue: Int,
    onRankChange: (Int) -> Unit,
    stripeValue: Int,
    onStripeChange: (Int) -> Unit,
    beltRankList: List<String>,
    beltStripeList: List<String>,
) {
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        // Belt Rank
        VerticalWheelPicker(
            items = beltRankList,
            initialIndex = rankValue,
            onSelected = { i, value -> onRankChange(i) },
            itemHeight = 44.dp,
            itemWidth  = 86.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Belt Stripe
        VerticalWheelPicker(
            items = beltStripeList,
            initialIndex = stripeValue,
            onSelected = { i, value -> onStripeChange(i) },
            itemHeight = 44.dp,
            itemWidth  = 86.dp
        )
    }
}

@Composable
private fun WeightWheel(
    intValue: Int,
    onIntChange: (Int) -> Unit,
    decValue: Int,
    onDecChange: (Int) -> Unit,
    onGenderChange: (Int) -> Unit,
    intRange: IntRange = 40..150,
    decRange: IntRange = 0..9,
) {
    val unitTextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)

    Row(verticalAlignment = Alignment.CenterVertically) {
        // 성별 휠
        VerticalWheelPicker(
            items = GENDER_LIST.map { it.displayName },
            initialIndex = 0,
            onSelected = { i, value -> onGenderChange(i) },
            itemHeight = 44.dp,
            itemWidth  = 86.dp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        // 정수 휠
        VerticalWheelPicker(
            items = intRange.map { it.toString() },
            initialIndex = intValue,
            onSelected = { i, value -> onIntChange(value.toInt()) },
            itemHeight = 44.dp,
            itemWidth  = 86.dp
        )
        Text(
            ".",
            modifier = Modifier.width(18.dp),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            textAlign = TextAlign.Center
        )
        // 소수 휠
        VerticalWheelPicker(
            items = decRange.map { it.toString() },
            initialIndex = decValue,
            onSelected = { i, value -> onDecChange(value.toInt()) },
            itemHeight = 44.dp,
            itemWidth  = 86.dp
        )
        Text(" kg", style = unitTextStyle, modifier = Modifier.padding(start = 6.dp))
    }
}