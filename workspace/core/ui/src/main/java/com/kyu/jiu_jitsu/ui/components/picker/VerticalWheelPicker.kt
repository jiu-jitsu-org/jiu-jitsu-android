package com.kyu.jiu_jitsu.ui.components.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyu.jiu_jitsu.ui.theme.CoolGray50
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlin.math.abs

@Composable
fun VerticalWheelPicker(
    items: List<String>,
    initialIndex: Int = 0,                 // 0..items.lastIndex
    onSelected: (index: Int, value: String) -> Unit,
    modifier: Modifier = Modifier,
    visibleCount: Int = 3,                 // 홀수 권장
    itemHeight: Dp = 44.dp,
    itemWidth: Dp = 72.dp,
    textSize: TextUnit = 20.sp,
    enableHaptics: Boolean = true
) {
    require(items.isNotEmpty()) { "items must not be empty" }
    require(visibleCount % 2 == 1) { "visibleCount must be odd" }
    val haptic = LocalHapticFeedback.current

    val state = rememberLazyListState(
        // 무한 루프가 아니므로 그대로 초기 포지션 사용
        initialFirstVisibleItemIndex = initialIndex
    )
    val fling = rememberSnapFlingBehavior(state)

    // 뷰포트 높이와 상/하 패딩(가운데 1칸을 위해)
    val viewportHeight = itemHeight * visibleCount
    val verticalPadding = itemHeight * (visibleCount / 2)

    // 현재 뷰포트 중앙에 가장 가까운 실제 아이템 인덱스 계산
    val centeredIndex by remember {
        derivedStateOf { currentCenteredIndex(state) }
    }.let { derived ->
        // 화면에 보이는 인덱스가 실제 아이템 범위를 벗어나지 않도록 클램핑
        derivedStateOf {
            val i = derived.value
            i.coerceIn(0, items.lastIndex)
        }
    }

    // 중앙 항목 변동 시 콜백 + 경미한 햅틱
    LaunchedEffect(state, items) {
        snapshotFlow { centeredIndex }
            .distinctUntilChanged()
            .collect { i ->
                onSelected(i, items[i])
                if (enableHaptics) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
    }
    // 스크롤 종료 시 확정 햅틱
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .distinctUntilChanged()
            .filter { !it }
            .collect {
                if (enableHaptics) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
    }

    Box(
        modifier = modifier
            .width(itemWidth)
            .height(viewportHeight),
        contentAlignment = Alignment.Center
    ) {

        // 가운데 선택 가이드(필요 시 색/선으로 변경)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeight)
                .background(color = CoolGray50, shape = RoundedCornerShape(10.dp))
        )

        LazyColumn(
            state = state,
            flingBehavior = fling,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = verticalPadding)
        ) {
            itemsIndexed(items) { index, label ->
                val dist = abs(index - centeredIndex)
                val isCenter = dist == 0
                val scale = if (isCenter) 1f else 0.86f
                val alpha = if (isCenter) 1f else 0.45f

                Box(
                    modifier = Modifier
                        .width(itemWidth)
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = textSize,
                        fontWeight = if (isCenter) FontWeight.SemiBold else FontWeight.Medium,
                        modifier = Modifier
                            .alpha(alpha)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                    )
                }
            }
        }

        // 위/아래 페이드
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
//                .background(
//                    Brush.verticalGradient(
//                        0f to Color.White,
//                        0.15f to Color.White,
//                        0.5f to Color.Transparent,
//                        0.85f to Color.White,
//                        1f to Color.White
//                    )
//                )
        )
    }
}

/** 뷰포트 중앙에 가장 가까운 LazyColumn 아이템 인덱스 */
private fun currentCenteredIndex(state: LazyListState): Int {
    val info = state.layoutInfo
    if (info.visibleItemsInfo.isEmpty()) return state.firstVisibleItemIndex
    val centerY = (info.viewportStartOffset + info.viewportEndOffset) / 2
    var closestIndex = state.firstVisibleItemIndex
    var minDist = Int.MAX_VALUE
    for (item in info.visibleItemsInfo) {
        val itemCenter = item.offset + item.size / 2
        val d = abs(itemCenter - centerY)
        if (d < minDist) {
            minDist = d; closestIndex = item.index
        }
    }
    return closestIndex
}