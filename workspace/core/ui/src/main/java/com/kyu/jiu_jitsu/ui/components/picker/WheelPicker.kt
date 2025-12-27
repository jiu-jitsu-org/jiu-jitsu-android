package com.kyu.jiu_jitsu.ui.components.picker

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.math.absoluteValue

@Composable
fun WheelPicker(
    items: List<String>,
    initial: Int = 0,
    onSelected: (index: Int, value: String) -> Unit,
    modifier: Modifier = Modifier,
    visibleCount: Int = 5,         // 화면에 보이는 개수(홀수 권장)
    itemHeight: Dp = 40.dp,
    itemWidth: Dp = 56.dp,
    textSize: TextUnit = 20.sp
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // 무한루프 인덱스 시작점 계산
//    val loop = remember(items) { items.size }
//    val base = remember(items) { Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2 % loop) }
//    val initialIndex = remember(initial, loop) { base + initial }

//    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val listState = rememberLazyListState()
    val fling = rememberSnapFlingBehavior(lazyListState = listState)


    // 중앙 셀 계산 및 구독: 항목이 바뀔 때마다 가벼운 햅틱
//    LaunchedEffect(listState, items) {
//        snapshotFlow { currentCenteredIndex(listState) }
//            .map { it % loop }
//            .distinctUntilChanged()
//            .collect { centered ->
//                onSelected(centered, items[centered])
//                // 스크롤 중 변경되는 틱 느낌
//                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
//            }
//    }
    LaunchedEffect(listState, items) {
        snapshotFlow { currentCenteredIndex(listState) }
//            .map { it % loop }
            .distinctUntilChanged()
            .collect { centered ->
                onSelected(centered, items[centered])
                // 스크롤 중 변경되는 틱 느낌
//                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
    }
    // 스크롤 종료 시 확정 햅틱
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { scrolling -> !scrolling }
            .collect {
//                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
    }

    val half = visibleCount / 2
    val boxHeight = itemHeight * visibleCount

    Box(
        modifier = modifier
            .width(itemWidth)
            .height(boxHeight),
        contentAlignment = Alignment.Center
    ) {
//        LazyRow(
//            state = listState,
//            flingBehavior = fling,
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxSize(),
//            contentPadding = PaddingValues(horizontal = itemWidth * half)
//        ) {
//            items(count = Int.MAX_VALUE) { rawIndex ->
//                val logicalIndex = (rawIndex % loop + loop) % loop
//                val center = currentCenteredIndex(listState)
//                val distance = (rawIndex - center).absoluteValue
//                val scale = animateFloatAsState(
//                    targetValue = if (distance == 0) 1.0f else 0.82f,
//                    label = "scale"
//                )
//                val alpha = animateFloatAsState(
//                    targetValue = if (distance == 0) 1.0f else 0.45f,
//                    label = "alpha"
//                )
//                Box(
//                    modifier = Modifier
//                        .width(itemWidth)
//                        .height(itemHeight),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = items[logicalIndex],
//                        fontSize = textSize,
//                        fontWeight = if (distance == 0) FontWeight.SemiBold else FontWeight.Medium,
//                        modifier = Modifier
//                            .alpha(alpha.value)
//                            .graphicsLayer {
//                                scaleX = scale.value
//                                scaleY = scale.value
//                            }
//                    )
//                }
//            }
//        }

        LazyRow(
            state = listState,
            flingBehavior = fling,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = itemWidth * half)
        ) {
            items(count = items.size) { index ->
                val center = currentCenteredIndex(listState)
                val distance = (index - center).absoluteValue
                val scale = animateFloatAsState(
                    targetValue = if (distance == 0) 1.0f else 0.82f,
                    label = "scale"
                )
                val alpha = animateFloatAsState(
                    targetValue = if (distance == 0) 1.0f else 0.45f,
                    label = "alpha"
                )
                Box(
                    modifier = Modifier
                        .width(itemWidth)
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[index],
                        fontSize = textSize,
                        fontWeight = if (distance == 0) FontWeight.SemiBold else FontWeight.Medium,
                        modifier = Modifier
                            .alpha(alpha.value)
                            .graphicsLayer {
                                scaleX = scale.value
                                scaleY = scale.value
                            }
                    )
                }
            }
        }

        // 가운데 가이드(선택 영역)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeight)
                .background(Color.Transparent)
        )

        // 위/아래 페이드 마스크
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.White,
                        0.15f to Color.White,
                        0.5f to Color.Transparent,
                        0.85f to Color.White,
                        1f to Color.White
                    )
                )
        )
    }
}

private fun currentCenteredIndex(state: LazyListState): Int {
    // LazyRow: 수평. firstVisibleItemScrollOffset로 중앙 판단.
    val layoutInfo = state.layoutInfo
    val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
    var closest = state.firstVisibleItemIndex
    var min = Int.MAX_VALUE
    layoutInfo.visibleItemsInfo.forEach { item ->
        val itemCenter = (item.offset + item.size / 2)
        val distance = (itemCenter - viewportCenter).absoluteValue
        if (distance < min) {
            min = distance
            closest = item.index
        }
    }
    return closest
}