package kr.bjj_oss.ui.belt

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntRect
import kr.bjj_oss.ui.R
import kotlin.math.cos
import kotlin.math.sin

internal enum class BeltSceneMode { Splash, Login, Greeting }

/** 앱이 보고한 분리 또는 가림 영역이다. 좌표 단위는 현재 창의 px이다. */
data class BeltFold(val boundsInWindow: IntRect, val horizontal: Boolean)

// 접힘 정보를 제공하지 않는 창은 하나의 연속 영역으로 배치한다.
val LocalBeltFold = staticCompositionLocalOf<BeltFold?> { null }

/**
 * 투명 여백까지 포함한 몸통 PNG의 원본 픽셀 정보다. [bandX]와 [bandY]는 눈을 얹을
 * 밴드의 중심이고 [topX]는 말풍선 앵커에 사용할 머리 꼭대기의 가로 좌표다.
 * 에셋을 교체하거나 투명 여백을 자르면 이 좌표와 밴드 폭을 함께 다시 측정해야 한다.
 */
internal data class BeltBody(
    val image: Int, val width: Float, val height: Float, val bandX: Float, val bandY: Float,
    val bandWidth: Float, val bandAngle: Float, val topX: Float,
)

/** 눈의 폭과 중심 오프셋은 화면에 그릴 밴드 폭에 대한 비율이다. [stretch]는 세로에만 적용한다. */
internal data class BeltEyes(
    val image: Int, val aspect: Float, val widthRatio: Float,
    val stretch: Float = 1f, val x: Float = 0f, val y: Float = 0f,
)

/**
 * [bandWidth], [centerX], [visible]은 폭 393을 기준으로 한 장면 좌표다. [centerX]는
 * 밴드 중심, [visible]은 가림선 위에 드러나는 높이다. [start]와 [jump]는 장면 시작부터의 초이며
 * [speed]는 등장 구간의 재생 시간에 곱하므로 값이 클수록 등장 시간이 길어진다.
 */
internal data class BeltMember(
    val index: Int, val body: BeltBody, val eyes: BeltEyes,
    val bandWidth: Float, val centerX: Float, val visible: Float, val tilt: Float, val z: Int,
    val mirrorBody: Boolean = false, val mirrorEyes: Boolean = false,
    val start: Float, val speed: Float, val jump: Float,
)

// 인덱스는 이미지 목록과 화자 선택에 공유한다. 겹쳐 그리는 순서는 각 멤버의 z로 정한다.
internal val beltMembers = listOf(
    BeltMember(0, BeltBody(R.drawable.belt_body_brown, 150f, 653f, 63.4f, 161.3f, 131f, .7f, 75.1f),
        BeltEyes(R.drawable.belt_eyes_lash, .7188f, .90f), 94f, 28f, 300f, 7f, 5,
        start = .91f, speed = 1.04f, jump = 2.25f),
    BeltMember(1, BeltBody(R.drawable.belt_body_white, 230f, 605f, 99.5f, 166.5f, 184f, 32.3f, 153f),
        BeltEyes(R.drawable.belt_eyes_curious, .7414f, .74f, x = -.12f, y = -.10f), 88f, 104f, 160f, 0f, 8,
        mirrorEyes = true, start = .87f, speed = .92f, jump = 2.20f),
    BeltMember(2, BeltBody(R.drawable.belt_body_black, 151f, 653f, 74.3f, 154.7f, 131f, .7f, 74.8f),
        BeltEyes(R.drawable.belt_eyes_sleepy, .303f, 1.12f, 1.18f), 96f, 190f, 312f, 1f, 5,
        mirrorEyes = true, start = .95f, speed = .98f, jump = 2.15f),
    BeltMember(3, BeltBody(R.drawable.belt_body_blue, 214f, 651f, 115.2f, 157.3f, 145.1f, 11.8f, 141f),
        BeltEyes(R.drawable.belt_eyes_glare, .362f, 1.18f, 1.40f), 86f, 270f, 170f, 0f, 8,
        mirrorBody = true, start = .93f, speed = .94f, jump = 2.20f),
    BeltMember(4, BeltBody(R.drawable.belt_body_purple, 150f, 654f, 63f, 159.5f, 131f, .7f, 75f),
        BeltEyes(R.drawable.belt_eyes_big, .5051f, 1.07f), 94f, 356f, 288f, -6f, 5,
        mirrorEyes = true, start = .98f, speed = 1.06f, jump = 2.25f),
)

/** 몸통과 머리는 장면 좌표, 눈과 회전 피벗은 몸통 사각형 내부 좌표로 보관한다. */
internal data class BeltPlacement(val body: Rect, val eye: Rect, val pivot: Offset, val head: Offset)

/**
 * 선택된 안전 영역의 실제 치수를 장면 좌표로 변환한다. 모든 입력은 dp에 대응하는 논리 단위다.
 * [boundary]는 몸통이 가려지는 y 좌표이고 [topInset]은 해당 영역 안에서 피해야 하는 상단 높이다.
 * 프레임 시간과 분리되어 창 크기, 힌지 또는 시트의 최종 높이가 바뀔 때만 다시 계산하면 된다.
 */
internal data class StageGeometry(
    val mode: BeltSceneMode, val width: Float, val height: Float, val boundary: Float, val topInset: Float,
) {
    // 넓고 낮은 창에서는 높이로 먼저 제약하고, 펼친 화면에서는 캐릭터 확대율을 제한한다.
    val scale = minOf(width / 393f, ((boundary - topInset) / 378f).coerceAtLeast(0f), 1.25f)
    val placements = beltMembers.map(::place)
    private val brandClearance = (placements.minOf { it.head.y } - topInset - 66f).coerceAtLeast(0f)
    val brandTop = if (mode == BeltSceneMode.Login) {
        topInset + brandClearance / 2f
    } else topInset + minOf(59f, brandClearance / 2f)
    // 짧은 영역에서는 로그인 조작부와 인사 문구에 쓸 공간을 확보하기 위해 브랜딩을 생략한다.
    val showBrand = when (mode) {
        BeltSceneMode.Splash -> true
        BeltSceneMode.Login -> boundary - topInset >= 120f
        BeltSceneMode.Greeting -> boundary - topInset >= 200f
    }
    // 세로로 긴 스플래시 원본 프레임을 가운데 맞추고, 시작 패턴과 로고의 위치를 여기서 계산한다.
    val frameHeight = width * 1200f / 555f
    val frameTop = (height - frameHeight) / 2f
    val splashPatternTop = frameTop + frameHeight * .1775f
    val patternRestTop = if (mode == BeltSceneMode.Login) boundary + 50f else splashPatternTop + 120f * scale

    private fun place(member: BeltMember): BeltPlacement {
        val body = member.body
        val eyes = member.eyes
        // 캐릭터별 원본 PNG 크기가 달라도 밴드가 명세상 너비로 보이도록 배율을 정한다.
        val p = member.bandWidth * scale / body.bandWidth
        val w = body.width * p
        val h = body.height * p
        // 몸통 밑부분이 가림선 아래까지 이어지도록 드러나는 높이에 상한을 둔다.
        val visible = minOf(member.visible * scale, ((h - 12f) / 1.12f).coerceAtLeast(0f))
        val bandX = body.bandX * p
        val centerX = width / 2f + (member.centerX - 393f / 2f) * scale
        val frameLeft = centerX - bandX
        val frameTop = boundary - visible
        // 머리의 최종 좌표에 몸통 반전과 기울기를 반영한다. 말풍선은 숨쉬기나 점프에 따라 흔들리지 않는다.
        val mirror = if (member.mirrorBody) -1 else 1
        val headDx = mirror * (body.topX - body.bandX) * p
        val angle = member.tilt * (Math.PI.toFloat() / 180f)
        val head = Offset(
            centerX + headDx * cos(angle) + visible * sin(angle),
            boundary + headDx * sin(angle) - visible * cos(angle),
        )
        val bandWidth = body.bandWidth * p
        // 눈은 몸통의 원본 밴드 중심을 기준으로 배치한 뒤 렌더러에서 밴드 각도와 깜빡임을 적용한다.
        val eyeWidth = bandWidth * eyes.widthRatio
        val eyeHeight = eyeWidth * eyes.aspect * eyes.stretch
        val eyeX = bandX + eyes.x * bandWidth * mirror - eyeWidth / 2f
        val eyeY = body.bandY * p + eyes.y * bandWidth - eyeHeight / 2f
        return BeltPlacement(
            Rect(frameLeft, frameTop, frameLeft + w, frameTop + h),
            Rect(eyeX, eyeY, eyeX + eyeWidth, eyeY + eyeHeight),
            Offset(bandX, visible), head,
        )
    }
}
