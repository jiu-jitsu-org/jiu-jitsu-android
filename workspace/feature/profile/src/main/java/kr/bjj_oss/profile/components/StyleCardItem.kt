package kr.bjj_oss.profile.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kr.bjj_oss.ui.theme.ColorComponents
import kr.bjj_oss.ui.theme.CoolGray25
import kr.bjj_oss.ui.theme.CoolGray75

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
        shape = RoundedCornerShape(24.dp),
        color = ColorComponents.List.Setting.Background,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val contentPadding = (maxWidth * 0.10f).coerceIn(15.dp, 26.dp)
            val iconSize = (maxWidth * 0.245f).coerceIn(40.dp, 62.dp)
            val iconShape = RoundedCornerShape((iconSize * 0.23f).coerceIn(12.dp, 16.dp))
            val labelTopSpacing = (maxWidth * 0.095f).coerceIn(14.dp, 26.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                // 아이템 아이콘
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .background(color = CoolGray25, shape = iconShape)
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

                Spacer(modifier = Modifier.height(labelTopSpacing))
                // Title
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.labelMedium,
                    color = CoolGray75,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = itemName ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = CoolGray75,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
