package com.kyu.jiu_jitsu.ui.components.card

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun DraggableFlipCard(
    modifier: Modifier = Modifier,
    front: @Composable () -> Unit,
    back:  @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) } // 0f..180f
    var size by remember { mutableStateOf(IntSize.Zero) }

    val density = LocalDensity.current
    val cameraDistPx = with(density) { 48.dp.toPx() }

    // 회전에 따른 음영(하이라이트) 강도 (0~1)
    val shade = remember {
        derivedStateOf {
            val r = rotation.value
            // 90도 부근에서 가장 어둡게
            1f - (kotlin.math.abs(r - 90f) / 90f)
        }
    }

    Box(
        modifier = modifier
            .onSizeChanged { size = it }
            .pointerInput(size) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            // 절반 넘으면 백(180), 아니면 프론트(0)로 스냅
                            val target = if (rotation.value >= 90f) 180f else 0f
                            rotation.animateTo(target, tween(350, easing = FastOutSlowInEasing))
                        }
                    }
                ) { change, drag ->
                    change.consume()
                    val w = size.width.takeIf { it > 0 } ?: return@detectDragGestures
                    // 드래그량을 각도로 환산 (튜닝 값 180 * (dx/w))
                    val delta = (drag.x / w) * 180f
                    scope.launch {
                        rotation.snapTo((rotation.value + delta).coerceIn(0f, 180f))
                    }
                }
            }
    ) {
        // FRONT
        Box(
            Modifier
                .matchParentSize()
                .graphicsLayer {
                    rotationY = rotation.value
                    cameraDistance = cameraDistPx
                }
                .alpha(if (rotation.value <= 90f) 1f else 0f)
        ) { front() }

        // BACK
        Box(
            Modifier
                .matchParentSize()
                .graphicsLayer {
                    rotationY = rotation.value + 180f
                    cameraDistance = cameraDistPx
                    scaleX = -1f
                }
                .alpha(if (rotation.value > 90f) 1f else 0f)
        ) { back() }

        // 회전 진행에 따른 그라데이션 음영 (입체감)
        Canvas(Modifier.matchParentSize()) {
            val alpha = (0.35f * shade.value).coerceIn(0f, 0.35f)
            drawRect(
                brush = Brush.linearGradient(
                    0f to Color.Black.copy(alpha = alpha),
                    1f to Color.Transparent
                )
            )
        }
    }
}
