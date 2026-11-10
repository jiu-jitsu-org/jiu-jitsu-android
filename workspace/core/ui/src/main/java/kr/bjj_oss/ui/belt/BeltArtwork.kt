package kr.bjj_oss.ui.belt

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import kr.bjj_oss.ui.R
import kotlin.random.Random

val BeltSceneBlue = Color(0xFF0091FF)

// 점 말풍선의 크기는 원본 70 × 42 도형에 0.68을 곱한 값이다. 꼬리 끝은 몸통 밖에 있다.
private const val DotWidth = 47.6f
private const val DotHeight = 28.56f
private const val DotTipX = -.68f
private const val DotTipY = 39.44f
private val dotSizes = floatArrayOf(9f, 10.5f, 8.5f)
private val dotY = floatArrayOf(1f, -1f, .5f)
private val drawOrder = beltMembers.sortedBy { it.z }

/** 로고, 패턴, 캐릭터, 점 말풍선처럼 상호작용이 없는 장식만 한 Canvas에서 그린다. */
@Composable
internal fun BeltArtwork(
    geometry: StageGeometry,
    time: State<Float>,
    motion: Boolean,
    speaker: Int?,
    idleSeed: Int,
    modifier: Modifier,
) {
    val pattern = painterResource(R.drawable.belt_crowd_pattern)
    val tagline = painterResource(R.drawable.belt_crowd_tagline)
    val logo = painterResource(R.drawable.belt_crowd_logo)
    val drawMembers = geometry.mode != BeltSceneMode.Splash && geometry.scale >= .26f
    // Painter와 캐릭터별 타이밍은 그리기 콜백 밖에서 준비한다. 저장된 시드로 순서를 재현하므로
    // 창 크기 변경이나 화면 재생성으로 캐릭터별 숨쉬기·깜빡임 타이밍이 달라지지 않는다.
    val bodies = if (drawMembers) beltMembers.map { painterResource(it.body.image) } else emptyList()
    val eyes = if (drawMembers) beltMembers.map { painterResource(it.eyes.image) } else emptyList()
    val idleTimings = remember(idleSeed) {
        val random = Random(idleSeed)
        List(beltMembers.size) {
            IdleTiming(random.nextFloat() * .9f, 2.4f + random.nextFloat() * .6f,
                .6f + random.nextFloat() * 2.4f, 2.6f + random.nextFloat() * 3f)
        }
    }
    Canvas(modifier) {
        // 스플래시는 시작 배치를, 모션을 끈 로그인은 등장 완료 배치를 사용한다.
        // 프레임 시간은 이 콜백에서 읽어 장식의 다시 그리기만 요청한다.
        val elapsed = when {
            geometry.mode == BeltSceneMode.Splash -> 0f
            motion -> time.value
            else -> 2.10f
        }
        val login = geometry.mode == BeltSceneMode.Login
        val speech = mutableListOf<Speech>()
        if (motion && login) chatter(elapsed, speech::add)
        if (motion && speaker != null && elapsed >= 2.63f) speech.add(Speech(speaker, 2.63f, 1.30f))

        // 논리 좌표로 계산한 크기와 오프셋을 그리기 직전에 px로 변환한다.
        scale(density, density, Offset.Zero) {
            clipRect(0f, 0f, geometry.width, geometry.height) {
                val patternTop = mix(geometry.splashPatternTop, geometry.patternRestTop, patternProgress(elapsed))
                // 인사 패턴은 캐릭터 뒤에, 로그인 패턴은 앞에 그려 시트 위쪽의 겹침 순서를 맞춘다.
                if (!login) drawCentered(pattern, geometry.width, geometry.width, patternTop)
                if (drawMembers) {
                    // 캐릭터 가림선은 시트의 최종 위치를 기준으로 고정한다. 28의 여유는 둥근 시트 모서리 아래까지 이어지는 영역이다.
                    val bottom = if (login) minOf(geometry.boundary + 28f, geometry.height) else geometry.height
                    clipRect(bottom = bottom, right = geometry.width) {
                        for (member in drawOrder) {
                            if (elapsed < member.start) continue
                            val index = member.index
                            val hop = speech.firstOrNull { it.speaker == index }?.let { speakingHop(elapsed - it.start) } ?: 0f
                            drawMember(member, geometry, bodies[index], eyes[index], idleTimings[index], elapsed, hop, motion)
                        }
                    }
                }
                if (login) drawCentered(pattern, geometry.width, geometry.width, patternTop)
                if (geometry.showBrand) {
                    val intro = brandProgress(elapsed)
                    val brandScale = minOf(1f, geometry.width / 182f)
                    // 태그라인 원본 SVG의 그룹 투명도를 벡터 전체에 한 번 적용한다.
                    drawCentered(tagline, geometry.width, mix(geometry.width * .405855f, 150f * brandScale, intro),
                        mix(geometry.frameTop + geometry.frameHeight * .437979f, geometry.brandTop, intro), .9f)
                    drawCentered(logo, geometry.width, mix(geometry.width * .415045f, 116f * brandScale, intro),
                        mix(geometry.frameTop + geometry.frameHeight * .468438f, geometry.brandTop + 24f, intro))
                }
                if (login && drawMembers && geometry.boundary >= geometry.brandTop + 114f) {
                    drawChatter(geometry, elapsed, motion, speech)
                }
            }
        }
    }
}

private fun DrawScope.drawCentered(painter: Painter, containerWidth: Float, width: Float, top: Float, alpha: Float = 1f) {
    val height = width * painter.intrinsicSize.height / painter.intrinsicSize.width
    drawPainter(painter, Rect((containerWidth - width) / 2f, top, (containerWidth + width) / 2f, top + height), alpha)
}

private fun DrawScope.drawPainter(painter: Painter, rect: Rect, alpha: Float = 1f) {
    translate(rect.left, rect.top) {
        with(painter) { draw(Size(rect.width, rect.height), alpha = alpha) }
    }
}

private fun DrawScope.drawMember(
    member: BeltMember,
    geometry: StageGeometry,
    body: Painter,
    eyes: Painter,
    idle: IdleTiming,
    time: Float,
    hop: Float,
    motion: Boolean,
) {
    val (frame, eye, pivot) = geometry.placements[member.index]
    val rise = risePose(time, member, pivot.y)
    val jump = if (geometry.mode == BeltSceneMode.Greeting) jumpPose(time, member, geometry.scale) else Pose()
    val breath = if (motion) breathe(time, idle) else Pose()
    val blink = if (motion) blink(time, idle) else 1f
    // 점에 적용되는 순서는 코드의 행렬 등록 순서와 반대다. 몸통 반전, 숨쉬기, 발화 이동,
    // 등장·점프 확대 및 이동, 캐릭터 기울기를 차례로 적용한 뒤 장면에 배치한다.
    // 눈은 자기 중심에서 별도로 반전·깜빡임·밴드 회전을 거친 뒤 몸통과 함께 움직인다.
    withTransform({
        translate(frame.left, frame.top)
        rotate(member.tilt, pivot)
        translate(0f, rise.lift + jump.lift)
        scale(rise.x * jump.x, rise.y * jump.y, pivot)
        translate(0f, hop)
        scale(breath.x, breath.y, pivot)
        scale(if (member.mirrorBody) -1f else 1f, 1f, pivot)
    }) {
        drawPainter(body, Rect(Offset.Zero, frame.size))
        withTransform({
            rotate(member.body.bandAngle, eye.center)
            scale(if (member.mirrorEyes) -1f else 1f, blink, eye.center)
        }) {
            drawPainter(eyes, eye)
        }
    }
}

private fun DrawScope.drawChatter(geometry: StageGeometry, time: Float, motion: Boolean, speech: List<Speech>) {
    var previous: Rect? = null
    // 모션 감소에서는 검정과 보라의 말풍선을 최종 크기와 불투명도로 계속 표시한다.
    val bubbles = if (motion) speech else listOf(Speech(2, 0f, 0f), Speech(4, 0f, 0f))
    for (bubble in bubbles) {
        val head = geometry.placements[bubble.speaker].head
        var right = bubble.speaker == 0
        fun bounds(pointsRight: Boolean): Rect {
            val tipX = if (pointsRight) DotTipX else DotWidth - DotTipX
            val target = head.x + if (pointsRight) 6f else -6f
            val left = (target - tipX).coerceIn(12f, (geometry.width - DotWidth - 12f).coerceAtLeast(12f))
            val top = maxOf(head.y - 6f - DotTipY, geometry.brandTop + 74f)
            return Rect(left, top, left + DotWidth, top + DotHeight)
        }
        var rect = bounds(right)
        // 먼저 나온 말풍선의 몸통에 6의 여유를 더해 비교한다. 겹치면 뒤에 나온 말풍선만 방향을 한 번 바꾼다.
        if (previous?.inflate(6f)?.overlaps(rect) == true) {
            right = !right
            rect = bounds(right)
        }
        previous = rect
        val age = time - bubble.start
        val pose = if (motion) bubblePose(age, bubble.duration) else BubblePose(1f, 1f, 0f, 0f, 1f)
        val pivot = Offset(if (right) DotTipX else DotWidth - DotTipX, DotTipY)
        withTransform({
            translate(rect.left, rect.top)
            rotate(pose.rotation, pivot)
            scale(pose.scaleX, pose.scaleY, pivot)
            translate(0f, pose.offset)
        }) {
            // 몸통·꼬리·그림자·점을 한 레이어로 합쳐 퇴장 투명도를 한 번 적용한다.
            // 꼬리의 방향을 바꿔도 점의 순서는 유지하도록 도형만 반전한다.
            drawIntoCanvas { canvas ->
                val paint = Paint().apply { alpha = pose.alpha }
                canvas.saveLayer(Rect(-12f, -12f, DotWidth + 12f, DotTipY + 14f), paint)
            }
            scale(if (right) 1f else -1f, 1f, Offset(DotWidth / 2f, 0f)) {
                drawPuff(DotWidth, DotHeight, Offset(5.1f, 31.62f), 3.808f, Offset(.34f, 37.4f), 2.04f)
            }
            var x = (DotWidth - dotSizes.sum() * .68f - 6f) / 2f
            for (index in dotSizes.indices) {
                val diameter = dotSizes[index] * .68f
                val wave = if (motion) dotWave(age, index) else 0f
                val radius = diameter / 2f * if (motion) dotScale(age, index) else 1f
                val y = DotHeight / 2f + dotY[index] * .68f - 3.5f * wave
                val alpha = if (motion) mix(.45f, 1f, wave) else 1f
                if (radius > 0f) drawCircle(BeltSceneBlue, radius, Offset(x + diameter / 2f, y), alpha)
                x += diameter + 3f
            }
            drawIntoCanvas { it.restore() }
        }
    }
}

/**
 * 점과 글 말풍선이 공유하는 원본 70 × 42 곡선이다. 폭이 넓어지면 오른쪽 제어점을 밀어
 * 둥근 양 끝의 비율을 유지한다. 꼬리 두 원을 같은 경로에 넣어 몸통과 한 번에 그림자를 그린다.
 * [shadowScale]은 px로 그리는 호출부에서 그림자 반경과 오프셋도 밀도에 맞춰 변환하기 위한 값이다.
 */
internal fun DrawScope.drawPuff(
    width: Float,
    height: Float,
    firstTail: Offset,
    firstRadius: Float,
    secondTail: Offset,
    secondRadius: Float,
    shadowScale: Float = 1f,
) {
    val unit = height / 42f
    val naturalWidth = 70f * unit
    val stretch = (width - naturalWidth).coerceAtLeast(0f)
    val xUnit = minOf(unit, width / 70f)
    fun x(value: Float) = value * xUnit + if (value >= 35f) stretch else 0f
    val puff = Path().apply {
        moveTo(x(22f), 1.5f * unit)
        cubicTo(x(30f), 0f, x(42f), 0f, x(50f), 1.5f * unit)
        cubicTo(x(62f), 3.5f * unit, x(70f), 10f * unit, x(69.5f), 21.5f * unit)
        cubicTo(x(69f), 33f * unit, x(61f), 40.5f * unit, x(48f), 41.5f * unit)
        cubicTo(x(39f), 42.3f * unit, x(30f), 42.3f * unit, x(21f), 41f * unit)
        cubicTo(x(9f), 39.5f * unit, x(1f), 33f * unit, x(1f), 21f * unit)
        cubicTo(x(1f), 9.5f * unit, x(10f), 3f * unit, x(22f), 1.5f * unit)
        close()
        addOval(Rect(firstTail, firstRadius))
        addOval(Rect(secondTail, secondRadius))
    }
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            color = Color.White
            asFrameworkPaint().setShadowLayer(4f * shadowScale, 0f, 4.8f * shadowScale, 0x17033969)
        }
        canvas.drawPath(puff, paint)
    }
}
