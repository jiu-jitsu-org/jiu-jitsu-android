package com.kyu.jiu_jitsu.profile.components.style

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.profile.model.StyleCardIndicator
import com.kyu.jiu_jitsu.ui.theme.TrueWhite

@Composable
fun CardIndicatorLayout(
    modifier: Modifier,
    items: List<StyleCardIndicator>,
    selectedIndex: Int = 0,
    onTabSelected: (index:Int) -> Unit,
) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Bottom,
        ) {
            itemsIndexed(items) { index, item ->
                val indicatorHeight by animateDpAsState(
                    targetValue = if (selectedIndex == index) 88.dp else 66.dp,
                    animationSpec = tween(durationMillis = 250),
                    label = "indicatorHeight"
                )
                val indicatorWidth by animateDpAsState(
                    targetValue = if (selectedIndex == index) 74.dp else 64.dp,
                    animationSpec = tween(durationMillis = 250),
                    label = "indicatorWidth"
                )
                Box(
                    modifier = Modifier
                        .height(indicatorHeight)
                        .width(indicatorWidth)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    item.color,
                                    item.color.copy(alpha = if (selectedIndex == index) 0.58f else 0.72f),
                                )
                            ),
                            shape = RoundedCornerShape(18.dp),
                        )
                        .clickable(
                            role = Role.Button,
                            onClick = { onTabSelected(index) }
                        ),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        modifier = Modifier.padding(top = 17.dp),
                        text = item.displayTitle(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = TrueWhite,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun StyleCardIndicator.displayTitle(): String = when (title) {
    "TOP" -> "탑"
    "GUARD" -> "가드"
    "SWEEPS" -> "스윕"
    "GUARD_PASSES" -> "패스"
    "TAKE_DOWNS" -> "테이"
    "ESCAPES" -> "이스"
    "CHOKES" -> "조르"
    "ARM_LOCKS" -> "팔"
    "LEG_LOCKS" -> "하체"
    else -> title
}
