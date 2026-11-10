package kr.bjj_oss.ui.belt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.bjj_oss.ui.theme.PretendardFont
import kotlin.math.roundToInt
import kotlin.random.Random

/** 앱에서 한 번 결정한 화자 인덱스와 현지화된 인사 문구다. */
data class BeltGreeting(val speaker: Int, val message: String)

private val staticTime: State<Float> = mutableStateOf(0f)
private val sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

/** 인증을 준비하는 동안 로그인 인트로의 시작 배치와 같은 브랜딩을 표시한다. */
@Composable
fun BeltSplash(modifier: Modifier = Modifier) {
    BeltStage(BeltSceneMode.Splash, staticTime, false, modifier)
}

/** 공용 장면에 호출부의 네이티브 로그인 버튼을 얹고, 버튼 영역에서 적용할 하단 여백을 전달한다. */
@Composable
fun BeltLogin(modifier: Modifier = Modifier, content: @Composable (bottomPadding: Dp) -> Unit) {
    val motion by rememberBeltMotionEnabled()
    val time = rememberBeltTime(motion, restartIdleOnResume = true)
    BeltStage(BeltSceneMode.Login, time, motion, modifier, sheetContent = content)
}

/** 앱의 홈 전환과 같은 경과 시간으로 캐릭터와 인사 말풍선을 그린다. */
@Composable
fun BeltGreetingScene(greeting: BeltGreeting, time: State<Float>, modifier: Modifier = Modifier) {
    BeltStage(BeltSceneMode.Greeting, time, true, modifier, greeting)
}

private data class StageAreas(val art: IntRect, val sheet: IntRect)

/**
 * 창 기준 힌지 좌표를 장면 기준으로 옮기고, 현재 콘텐츠 영역과 만나는 부분을 피해서 배치 영역을 정한다.
 * 가로 힌지는 위에 장식과 인사를, 아래에 로그인 시트를 둔다. 세로 힌지는 더 넓은 한쪽 영역을
 * 사용하고, 폭이 같으면 RTL 여부를 반영해 레이아웃 시작 쪽을 선택한다.
 */
private fun stageAreas(bounds: IntRect, fold: BeltFold?, origin: Offset, rtl: Boolean): StageAreas {
    if (fold == null) return StageAreas(bounds, bounds)
    val hinge = fold.boundsInWindow.translate(-origin.x.roundToInt(), -origin.y.roundToInt())
    if (fold.horizontal) {
        if (hinge.right <= bounds.left || hinge.left >= bounds.right ||
            hinge.bottom <= bounds.top || hinge.top >= bounds.bottom
        ) return StageAreas(bounds, bounds)
        val upper = IntRect(bounds.left, bounds.top, bounds.right, hinge.top.coerceAtLeast(bounds.top))
        val lower = IntRect(bounds.left, hinge.bottom.coerceAtMost(bounds.bottom), bounds.right, bounds.bottom)
        // 힌지가 콘텐츠 가장자리에 걸치면 실제로 남아 있는 한 영역에 모아 배치한다.
        return when {
            upper.height == 0 -> StageAreas(lower, lower)
            lower.height == 0 -> StageAreas(upper, upper)
            else -> StageAreas(upper, lower)
        }
    }
    if (hinge.bottom <= bounds.top || hinge.top >= bounds.bottom ||
        hinge.right <= bounds.left || hinge.left >= bounds.right
    ) return StageAreas(bounds, bounds)
    val left = IntRect(bounds.left, bounds.top, hinge.left.coerceAtLeast(bounds.left), bounds.bottom)
    val right = IntRect(hinge.right.coerceAtMost(bounds.right), bounds.top, bounds.right, bounds.bottom)
    val pane = if (right.width > left.width || (right.width == left.width && rtl)) right else left
    return StageAreas(pane, pane)
}

@Composable
private fun BeltStage(
    mode: BeltSceneMode,
    time: State<Float>,
    motion: Boolean,
    modifier: Modifier,
    greeting: BeltGreeting? = null,
    sheetContent: (@Composable (Dp) -> Unit)? = null,
) {
    val insets = WindowInsets.safeDrawing
    val fold = LocalBeltFold.current
    var origin by remember { mutableStateOf(Offset.Zero) }
    // 화면 재생성 시 캐릭터별 숨쉬기와 깜빡임 패턴을 이어가는 데 필요한 시드만 저장한다.
    val idleSeed = rememberSaveable { Random.nextInt() }
    SubcomposeLayout(
        modifier
            .background(BeltSceneBlue)
            .clipToBounds()
            .onGloballyPositioned { origin = it.positionInWindow() },
    ) { constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        // 현재 컴포저블의 제약을 사용한다. 파란 배경은 전체를 채우고, 좌우 안전 인셋은
        // 장식과 조작부에 적용한다. 상하 인셋은 선택된 영역의 좌표에 맞춰 별도로 반영한다.
        val left = insets.getLeft(this, layoutDirection).coerceIn(0, width)
        val right = (width - insets.getRight(this, layoutDirection)).coerceIn(left, width)
        val insetTop = insets.getTop(this).coerceIn(0, height)
        val insetBottom = insets.getBottom(this).coerceIn(0, height - insetTop)
        val safeBottom = height - insetBottom
        val bounds = IntRect(left, 0, right, height)
        val (art, sheetArea) = stageAreas(bounds, fold, origin, layoutDirection == LayoutDirection.Rtl)
        val sameArea = art == sheetArea
        val sheetTop = maxOf(sheetArea.top, insetTop)
        val sheetHeight = (sheetArea.bottom - sheetTop).coerceAtLeast(0)
        val sheetWidth = minOf(sheetArea.width, 560.dp.roundToPx())
        // 같은 측정 단계에서 버튼 시트를 먼저 잰다. 실제 버튼 높이와 글꼴 배율, 스크롤 제약을
        // 반영한 최종 시트 위치를 캐릭터 가림선으로 사용한다.
        val sheet = if (sheetContent != null && sheetWidth > 0 && sheetHeight > 0) {
            // 이 영역이 창의 안전 하단을 넘어가는 양만 하단 여백에 반영하고, 최소 여백을 보장한다.
            val bottomPadding = maxOf(16.dp, (sheetArea.bottom - safeBottom).coerceAtLeast(0).toDp())
            subcompose("sheet") {
                Box(Modifier.clip(sheetShape).background(Color.White)) {
                    sheetContent(bottomPadding)
                }
            }.single().measure(Constraints(minWidth = sheetWidth, maxWidth = sheetWidth, maxHeight = sheetHeight))
        } else null
        val finalSheetY = sheetArea.bottom - (sheet?.height ?: 0)
        val artWidth = minOf(art.width, 560.dp.roundToPx())
        val artTopInset = (insetTop - art.top).coerceIn(0, art.height)
        // 시트를 같은 영역에 그리면 최종 시트 상단이 가림선이다. 테이블톱에서는 위쪽 영역의
        // 하단을 사용하며, 인사는 상단 영역과 창의 안전 하단 중 먼저 끝나는 지점을 사용한다.
        val boundary = when {
            sheet != null && sameArea -> finalSheetY - art.top
            mode == BeltSceneMode.Greeting -> (safeBottom - art.top).coerceIn(0, art.height)
            else -> art.height
        }
        val artPlaceable = if (artWidth > 0 && art.height > 0) {
            val geometry = StageGeometry(
                mode, artWidth.toDp().value, art.height.toDp().value, boundary.toDp().value,
                artTopInset.toDp().value,
            )
            subcompose("art") {
                BeltStageArtwork(geometry, time, motion, greeting, idleSeed)
            }.single().measure(Constraints.fixed(artWidth, art.height))
        } else null

        layout(width, height) {
            // 힌지를 창의 물리 좌표로 다뤘으므로 여기서 좌우 위치를 다시 뒤집지 않는다.
            artPlaceable?.place(art.left + (art.width - artWidth) / 2, art.top)
            sheet?.placeWithLayer(sheetArea.left + (sheetArea.width - sheetWidth) / 2, finalSheetY) {
                // 최종 배치는 고정하고 레이어만 위로 올린다. 매 프레임 버튼을 다시 측정할 필요가 없다.
                translationY = if (motion) sheet.height * (1f - sheetProgress(time.value)) else 0f
            }
        }
    }
}

@Composable
private fun BeltStageArtwork(
    geometry: StageGeometry,
    time: State<Float>,
    motion: Boolean,
    greeting: BeltGreeting?,
    idleSeed: Int,
) {
    // 글 말풍선은 2.63초부터 퇴장이 끝나는 4.21초까지 구성한다. 중간 프레임에는 레이어만 갱신한다.
    val showGreeting by remember(time, greeting) {
        derivedStateOf { greeting != null && time.value >= 2.63f && time.value < 4.21f }
    }
    Layout(
        modifier = Modifier.clipToBounds(),
        content = {
            BeltArtwork(geometry, time, motion, greeting?.speaker, idleSeed, Modifier)
            if (showGreeting && greeting != null && geometry.width >= 84f) {
                GreetingBubble(greeting, geometry)
            }
        },
    ) { measurables, constraints ->
        val art = measurables[0].measure(constraints)
        // 좌우 14dp씩 비워 두고 문구를 실제 글꼴 크기로 측정한다. 좁은 창에서는 텍스트가 줄바꿈된다.
        val bubble = measurables.getOrNull(1)?.measure(
            Constraints(maxWidth = (constraints.maxWidth - 28.dp.roundToPx()).coerceAtLeast(0)),
        )
        layout(art.width, art.height) {
            art.place(0, 0)
            if (bubble != null && greeting != null) {
                val head = geometry.placements[greeting.speaker].head
                val headX = head.x.dp.toPx()
                val opensLeft = head.x > geometry.width / 2f
                val margin = 14.dp.toPx()
                val left = (headX - bubble.width * if (opensLeft) .7f else .3f)
                    .coerceIn(margin, (art.width - margin - bubble.width).coerceAtLeast(margin))
                val tail = tailCenter(headX - left, bubble.width.toFloat(), 20.dp.toPx())
                val minTop = (if (geometry.showBrand) geometry.brandTop + 74f else geometry.topInset + 4f).dp.toPx()
                val maxTop = (geometry.boundary - 16f).dp.toPx() - bubble.height
                // 브랜딩 아래에서 꼬리까지 들어갈 높이가 있을 때만 배치한다. 꼬리 방향을 피벗에도 반영한다.
                if (maxTop >= minTop) {
                    val top = (head.y.dp.toPx() - bubble.height - margin).coerceIn(minTop, maxTop)
                    bubble.placeWithLayer(left.roundToInt(), top.roundToInt()) {
                        val pose = bubblePose(time.value - 2.63f, 1.30f)
                        transformOrigin = TransformOrigin(tail / bubble.width, 1.2f)
                        scaleX = pose.scaleX
                        scaleY = pose.scaleY
                        rotationZ = pose.rotation
                        translationY = pose.offset.dp.toPx()
                        alpha = pose.alpha
                    }
                }
            }
        }
    }
}

@Composable
private fun GreetingBubble(greeting: BeltGreeting, geometry: StageGeometry) {
    val head = geometry.placements[greeting.speaker].head
    val opensLeft = head.x > geometry.width / 2f
    Box(
        Modifier
            .heightIn(min = 40.dp)
            .drawBehind {
                val margin = 14.dp.toPx()
                val headX = head.x.dp.toPx()
                val left = (headX - size.width * if (opensLeft) .7f else .3f)
                    .coerceIn(margin, (geometry.width.dp.toPx() - margin - size.width).coerceAtLeast(margin))
                val tail = tailCenter(headX - left, size.width, 20.dp.toPx())
                val lean = if (opensLeft) 4.dp.toPx() else -4.dp.toPx()
                drawPuff(
                    size.width, size.height, Offset(tail, size.height), 6.dp.toPx(),
                    Offset(tail + lean, size.height + 11.dp.toPx()), 3.dp.toPx(),
                    shadowScale = density,
                )
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        // 문구는 네이티브 Text로 그려 글꼴 배율과 접근성 탐색을 지원한다. 몸통과 꼬리는 배경 장식이다.
        Text(
            greeting.message,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            color = Color(0xFF292A2E),
            fontFamily = PretendardFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            letterSpacing = (-.3).sp,
        )
    }
}

private fun tailCenter(raw: Float, width: Float, padding: Float): Float {
    val margin = minOf(padding, width / 2f)
    return raw.coerceIn(margin, width - margin)
}
