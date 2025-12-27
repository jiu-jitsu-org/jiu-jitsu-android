package com.kyu.jiu_jitsu.profile.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.CoolGray25
import com.kyu.jiu_jitsu.ui.theme.CoolGray75

@Composable
fun StyleCardItem(
    modifier: Modifier,
    @DrawableRes iconRes: Int?,
    @StringRes titleRes: Int,
    itemName: String?,
    onClickEvent: () -> Unit,
) {
    Surface(
        modifier = modifier
            .clickable(
                role = Role.Button,
                onClick = onClickEvent,
            ),
        shadowElevation = 3.dp,
        shape = RoundedCornerShape(16.dp),
        color = ColorComponents.List.Setting.Background,
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            // 아이템 아이콘
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color = CoolGray25, shape = RoundedCornerShape(14.dp))
            ) {
                iconRes?.let {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(it),
                        contentDescription = "MyStyleDefault",
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            // Title
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleMedium,
                color = CoolGray75
            )

            Text(
                text = itemName ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = CoolGray75
            )
        }
    }
}