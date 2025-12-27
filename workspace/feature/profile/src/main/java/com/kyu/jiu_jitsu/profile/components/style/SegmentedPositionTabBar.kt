package com.kyu.jiu_jitsu.profile.components.style

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.profile.screen.StyleTabItem
import com.kyu.jiu_jitsu.ui.R

/**
 *  특기/최애를 위한 커스텀 Segmented TabBar
 *  @param tabs 탭 아이템 (특기/최애)
 *  @param selectedIndex 선택된 탭
 *  @param onTabSelected
 *  @param modifier
 *  @param containerColor
 *  @param indicatorColor
 */
@Composable
fun SegmentedPositionTabBar(
    tabs: List<StyleTabItem>,
    selectedIndex: Int,
    onTabSelected: (index:Int) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFE6E8EE),
    indicatorColor: Color = Color.White,
) {
    BoxWithConstraints(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(containerColor)
    ) {
        val tabCount = tabs.size
        val tabWidth: Dp = maxWidth / tabCount

        val targetOffset: Dp = tabWidth * selectedIndex

        // 인디케이터 X 오프셋을 애니메이션
        val indicatorOffset by animateDpAsState(
            targetValue = targetOffset,
            animationSpec = tween(durationMillis = 250),
            label = "indicatorOffset"
        )

        // 아래에 깔리는 흰색 pill 인디케이터
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(tabWidth)
                .offset(x = indicatorOffset)
                .padding(6.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(indicatorColor)
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, item ->
                val isSelected = (selectedIndex == index)

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFF111827) else Color(0xFF9CA3AF),
                    animationSpec = tween(200),
                    label = "textColor"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            if (!isSelected) onTabSelected(index)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource( if (item is StyleTabItem.Best) R.string.common_my_best else R.string.common_my_favorite),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = textColor
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.selectedItem,
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}