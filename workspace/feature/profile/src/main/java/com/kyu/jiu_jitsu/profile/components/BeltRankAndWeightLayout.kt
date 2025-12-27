package com.kyu.jiu_jitsu.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.data.model.BELT_RANK
import com.kyu.jiu_jitsu.data.model.BELT_STRIPE
import com.kyu.jiu_jitsu.data.model.CommunityProfileInfo
import com.kyu.jiu_jitsu.profile.R
import com.kyu.jiu_jitsu.ui.components.button.PrimaryButton
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.CoolGray50
import com.kyu.jiu_jitsu.ui.theme.TrueWhite
import com.kyu.jiu_jitsu.ui.theme.White
import com.kyu.jiu_jitsu.ui.theme.drawableRes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BeltRankAndWeightLayout(
    beltRank: BELT_RANK? = null,
    beltStripe: BELT_STRIPE? = null,
    weightKg: Double? = null,
    isWeightHidden: Boolean = false,
    onSaveBeltWeightClick: () -> Unit = {},
    onSaveWeightHiddenClick: (isWeightHidden: Boolean) -> Unit = {},
) {
    if (beltRank == null && weightKg == null) {
        /** 벨트와 체급 등록하기 */
        Column (
            modifier = Modifier
                .height(195.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            Image(
                modifier = Modifier
                    .height(56.dp)
                    .width(106.dp),
                painter = painterResource(R.drawable.ic_belt_and_weight_default),
                contentDescription = "Image"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.profile_belt_rank_and_weight_info),
                style = MaterialTheme.typography.titleSmall,
                color = ColorComponents.TextFieldDisplay.Default.Title,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = stringResource(R.string.profile_belt_rank_and_weight_save),
                onClick = onSaveBeltWeightClick
            )

        }
    } else {
        Row(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    role = Role.Button,
                    indication = ripple(bounded = false),
                    onClick = onSaveBeltWeightClick,
                ),
        ) {
            /** 벨트 */
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(com.kyu.jiu_jitsu.ui.R.string.common_belt),
                    style = MaterialTheme.typography.titleSmall,
                    color = ColorComponents.TextFieldDisplay.Default.Title,
                )
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = TrueWhite,shape = RoundedCornerShape(10.dp)),
                    painter = painterResource(beltRank?.drawableRes() ?: com.kyu.jiu_jitsu.ui.R.drawable.ic_white_belt),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Belt Icon"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = beltRank?.displayName ?: "",
                        style = MaterialTheme.typography.titleSmall,
                        color = ColorComponents.TextFieldDisplay.Default.Title,
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = beltStripe?.displayName ?: "",
                        style = MaterialTheme.typography.titleSmall,
                        color = ColorComponents.TextFieldDisplay.Default.Title,
                    )
                }

            }

            /** 체급 */
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(com.kyu.jiu_jitsu.ui.R.string.common_weight),
                    style = MaterialTheme.typography.titleSmall,
                    color = ColorComponents.TextFieldDisplay.Default.Title,
                )
                if (isWeightHidden) {
                    // 가려진 상태
                    Text(
                        text = stringResource(R.string.profile_weight_hide),
                        style = MaterialTheme.typography.titleSmall,
                        color = ColorComponents.TextFieldDisplay.Default.Title,
                    )
                } else {
                    // 보이는 상태
                    Text(
                        text = (weightKg?:0.0).toString() + "kg",
                        style = MaterialTheme.typography.titleSmall,
                        color = ColorComponents.TextFieldDisplay.Default.Title,
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            color = ColorComponents.Button.Neutral.DefaultBg,
                            shape = RoundedCornerShape(10.dp),
                        )
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            role = Role.Button,
                            indication = ripple(bounded = false),
                            onClick = {
                                onSaveWeightHiddenClick(!isWeightHidden)
                            }
                        )
                ) {
                    Text(
                        text = stringResource(if (isWeightHidden) R.string.profile_weight_show_btn else R.string.profile_weight_hide_btn),
                        style = MaterialTheme.typography.titleSmall,
                        color = ColorComponents.TextFieldDisplay.Default.Title,
                    )
                }
            }
        }
    }

}