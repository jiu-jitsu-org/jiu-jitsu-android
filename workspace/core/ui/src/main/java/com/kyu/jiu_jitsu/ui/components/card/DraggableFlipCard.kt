package com.kyu.jiu_jitsu.ui.components.card

import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@SuppressLint("UnrememberedMutableState")
@Composable
fun DraggableFlipCard(
    modifier: Modifier = Modifier,
    // 한 번 놓았을 때 스냅되는 애니메이션 스펙
    snapSpec: AnimationSpec<Float> = tween(350, easing = FastOutSlowInEasing),
    // 드래그 대비 회전 각 속도(1.0 = 가로폭만큼 드래그하면 180도)
    degreesPerWidth: Float = 180f,
    front: @Composable () -> Unit,
    back:  @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    var size by remember { mutableStateOf(IntSize.Zero) }

    // 누적 회전 각도(제약 없음: …, -180, 0, 180, 360, …)
    val rotationDeg = remember { Animatable(0f) }

    // 카메라 거리(px) – dp를 px로 변환
    val cameraDistancePx = with(LocalDensity.current) { 48.dp.toPx() } // 카드가 클수록 값 ↑ 추천

    // 0~360으로 정규화된 각도
    val angleMod by derivedStateOf {
        val a = rotationDeg.value % 360f
        if (a < 0f) a + 360f else a
    }

    // 앞/뒤 가시성: 0~90, 270~360 → 앞면 / 90~270 → 뒷면
    val frontAlpha by derivedStateOf { if (angleMod <= 90f || angleMod >= 270f) 1f else 0f }
    val backAlpha  by derivedStateOf { 1f - frontAlpha }

    // 90° 부근에서 가장 진하게 보이는 음영
    val shadeAlpha by derivedStateOf {
        (1f - (abs(angleMod - 90f) / 90f)).coerceIn(0f, 1f) * 0.35f
    }

    // 스냅 타깃: 가장 가까운 180° 배수로
    suspend fun snapToNearestHalfTurn() {
        val k = (rotationDeg.value / 180f).roundToInt() // 가장 가까운 180의 배수 계수
        rotationDeg.animateTo(180f * k, snapSpec)
    }

    Box(
        modifier = modifier
            .onSizeChanged { size = it }
            .pointerInput(size) {
                detectDragGestures(
                    onDragEnd = { scope.launch { snapToNearestHalfTurn() } }
                ) { change, drag ->
                    change.consume()
                    val w = size.width.takeIf { it > 0 } ?: return@detectDragGestures
                    // 좌↔우 어느 쪽이든 각도를 누적 (제자리에서 뒤집힘)
                    val deltaAngle = (drag.x / w) * degreesPerWidth
                    scope.launch {
                        rotationDeg.snapTo(rotationDeg.value + deltaAngle)
                    }
                }
            }
    ) {
        // FRONT
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    transformOrigin = TransformOrigin(0.5f, 0.5f) // 중심 축 → 제자리 플립
                    rotationY = angleMod
                    cameraDistance = cameraDistancePx
                }
                .then(Modifier) // 가독용
                .graphicsLayer { alpha = frontAlpha }
        ) {
            front()
        }

        // BACK (거울상 방지: scaleX = -1)
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                    rotationY = angleMod + 180f
                    scaleX = -1f
                    cameraDistance = cameraDistancePx
                }
                .graphicsLayer { alpha = backAlpha }
        ) {
            back()
        }

        // 진행 기반 그라데이션 음영(살짝 입체감)
        Canvas(Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.linearGradient(
                    0f to Color.Black.copy(alpha = shadeAlpha),
                    1f to Color.Transparent
                ),
                size = Size(size.width.toFloat(), size.height.toFloat())
            )
        }
    }
}

