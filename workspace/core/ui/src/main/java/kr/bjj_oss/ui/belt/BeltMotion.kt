package kr.bjj_oss.ui.belt

import android.animation.ValueAnimator
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlin.math.min

/** 인사 시작부터 홈이 완전히 덮을 때까지의 초다. 홈 이동은 3.95초부터 0.52초 동안 진행한다. */
const val BeltGreetingDuration = 4.47f

private val easeInOut = CubicBezierEasing(.42f, 0f, .58f, 1f)
private val easeOut = CubicBezierEasing(0f, 0f, .58f, 1f)
private val easeIn = CubicBezierEasing(.42f, 0f, 1f, 1f)
private val riseFirst = CubicBezierEasing(.35f, 0f, .25f, 1f)
private val sheetEase = CubicBezierEasing(.2f, .8f, .2f, 1f)
private val brandEase = CubicBezierEasing(.6f, 0f, .2f, 1f)
private val patternEase = CubicBezierEasing(.55f, 0f, .3f, 1f)

/** Canvas와 레이어에서 직접 구하는 모션도 기기의 애니메이션 사용 여부를 따르도록 관찰한다. */
@Composable
fun rememberBeltMotionEnabled(): State<Boolean> {
    val resolver = LocalContext.current.contentResolver
    val enabled = remember { mutableStateOf(ValueAnimator.areAnimatorsEnabled()) }
    DisposableEffect(resolver) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                enabled.value = ValueAnimator.areAnimatorsEnabled()
            }
        }
        resolver.registerContentObserver(
            Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE), false, observer,
        )
        enabled.value = ValueAnimator.areAnimatorsEnabled()
        onDispose { resolver.unregisterContentObserver(observer) }
    }
    return enabled
}

/**
 * 장면 전체가 공유하는 경과 시간을 초 단위로 제공한다. 시작점을 시스템 uptime으로 저장하므로
 * 시계 설정 변경과 무관하고, 같은 화면을 재생성하면 저장한 시작점에서 현재 포즈를 다시 구할 수 있다.
 * Compose 프레임마다 값을 갱신하되 생명주기가 STARTED 아래로 내려가면 갱신 작업을 취소한다.
 * 호출부는 이 상태를 그리기 또는 레이어 블록에서 읽어 화면 전체의 프레임별 재구성을 피한다.
 */
@Composable
fun rememberBeltTime(
    enabled: Boolean,
    limit: Float = Float.POSITIVE_INFINITY,
    restartIdleOnResume: Boolean = false,
): State<Float> {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var epoch by rememberSaveable { mutableLongStateOf(SystemClock.uptimeMillis()) }
    val time = remember { mutableFloatStateOf(elapsedSince(epoch, limit)) }
    LaunchedEffect(lifecycle, enabled, limit, restartIdleOnResume) {
        if (!enabled) return@LaunchedEffect
        var startedInThisComposition = false
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            val now = SystemClock.uptimeMillis()
            if (epoch > now) epoch = now
            // 기존 컴포지션으로 돌아왔고 인트로가 끝났다면 대기 구간부터 다시 시작한다.
            // 새 컴포지션의 첫 실행은 저장한 시작점을 사용하므로 화면 재생성 중인 장면은 이어진다.
            if (restartIdleOnResume && startedInThisComposition && time.floatValue >= 2.10f) {
                epoch = now - 2_100L
                time.floatValue = 2.10f
            }
            startedInThisComposition = true
            while (time.floatValue < limit) {
                withFrameNanos { time.floatValue = elapsedSince(epoch, limit) }
            }
        }
    }
    return time
}

private fun elapsedSince(epoch: Long, limit: Float): Float =
    min(((SystemClock.uptimeMillis() - epoch).coerceAtLeast(0L)) / 1000f, limit)

internal fun mix(start: Float, end: Float, fraction: Float) = start + (end - start) * fraction

// 트랙 시작 전과 종료 후에는 각각 시작값과 최종값을 그대로 유지한다.
private fun progress(time: Float, start: Float, duration: Float) =
    ((time - start) / duration).coerceIn(0f, 1f)

internal fun sheetProgress(time: Float) = sheetEase.transform(progress(time, .60f, .60f))
internal fun brandProgress(time: Float) = brandEase.transform(progress(time, .45f, .76f))
internal fun patternProgress(time: Float) = patternEase.transform(progress(time, .60f, 1.10f))
fun beltHomeProgress(time: Float) = brandEase.transform(progress(time, 3.95f, .52f))

/** [lift]는 y축 이동량으로 양수면 아래로 움직인다. [x]와 [y]는 피벗 기준 축별 확대율이다. */
internal data class Pose(val lift: Float = 0f, val x: Float = 1f, val y: Float = 1f)
private data class PoseKey(val at: Float, val pose: Pose)

// 등장 트랙의 lift는 가림선 위에 드러난 높이에 대한 비율이다. 첫 프레임에는 별도로 화면 아래의 시작점을 쓴다.
private val riseKeys = listOf(
    PoseKey(0f, Pose()), PoseKey(.55f, Pose(-.06f, .955f, 1.055f)),
    PoseKey(.72f, Pose(.012f, 1.03f, .97f)), PoseKey(.86f, Pose(-.008f, .99f, 1.01f)),
    PoseKey(1f, Pose()),
)
// 점프 트랙의 lift는 폭 393에서의 논리 좌표다. 실제 장면에서는 배율을 곱해 같은 비율로 뛰게 한다.
private val jumpKeys = listOf(
    PoseKey(0f, Pose()), PoseKey(.15f, Pose(0f, 1.05f, .94f)),
    PoseKey(.50f, Pose(-18f, .98f, 1.03f)), PoseKey(.80f, Pose(0f, 1.04f, .96f)),
    PoseKey(1f, Pose()),
)

private fun samplePose(keys: List<PoseKey>, at: Float, liftScale: Float, initialLift: Float? = null): Pose {
    val index = (keys.indexOfFirst { it.at >= at } - 1).coerceAtLeast(0)
    val (start, from) = keys[index]
    val (end, to) = keys[index + 1]
    val fraction = progress(at, start, end - start)
    // 등장에는 전체 트랙 이징과 첫 두 구간의 이징을 겹쳐 적용한다. 점프의 키 사이는 선형이다.
    val eased = when {
        initialLift == null -> fraction
        index == 0 -> riseFirst.transform(fraction)
        index == 1 -> easeInOut.transform(fraction)
        else -> fraction
    }
    val liftFrom = if (index == 0 && initialLift != null) initialLift else from.lift * liftScale
    return Pose(mix(liftFrom, to.lift * liftScale, eased), mix(from.x, to.x, eased), mix(from.y, to.y, eased))
}

internal fun risePose(time: Float, member: BeltMember, visible: Float): Pose =
    samplePose(riseKeys, easeInOut.transform(progress(time, member.start, .988f * member.speed)), visible, visible + 32f)

internal fun jumpPose(time: Float, member: BeltMember, scale: Float): Pose =
    samplePose(jumpKeys, easeOut.transform(progress(time, member.jump, .44f)), scale)

internal data class IdleTiming(val breatheDelay: Float, val halfPeriod: Float, val firstBlink: Float, val blinkWait: Float)

// 한 주기에서 기본 크기와 최대 크기를 오가며 양 끝에서 완만하게 방향을 바꾼다.
internal fun breathe(time: Float, timing: IdleTiming): Pose {
    val cycle = (time - 2.10f - timing.breatheDelay).coerceAtLeast(0f) % (2 * timing.halfPeriod)
    val peak = easeInOut.transform(1f - kotlin.math.abs(cycle / timing.halfPeriod - 1f))
    return Pose(x = mix(1f, 1.004f, peak), y = mix(1f, 1.006f, peak))
}

/** 눈을 0.075초 동안 세로 8%까지 닫고, 0.095초 동안 연 뒤 다음 깜빡임까지 뜬 상태를 유지한다. */
internal fun blink(time: Float, timing: IdleTiming): Float {
    val elapsed = time - 2.10f - timing.firstBlink
    if (elapsed < 0f) return 1f
    val local = elapsed % (.170f + timing.blinkWait)
    return if (local < .075f) mix(1f, .08f, easeInOut.transform(progress(local, 0f, .075f)))
    else mix(.08f, 1f, easeInOut.transform(progress(local, .075f, .095f)))
}

internal fun speakingHop(age: Float): Float =
    if (age < 0f || age >= .38f) 0f
    else -4f * easeInOut.transform(1f - kotlin.math.abs(age / .19f - 1f))

/** [speaker]는 캐릭터 인덱스다. [duration]은 등장부터 퇴장을 시작하기까지의 초다. */
internal data class Speech(val speaker: Int, val start: Float, val duration: Float)

private val firstSpeech = Speech(0, 2.95f, 1.80f)
private val secondSpeech = Speech(2, 4.25f, 1.70f)
private val speakers = intArrayOf(4, 0, 2)

/**
 * 현재 시각에 그려야 할 로그인 말풍선을 시작 순서대로 전달한다. 첫 두 말풍선은 겹치며,
 * 이후에는 보라·갈색·검정 순으로 2.8초마다 하나씩 등장한다. 퇴장 0.28초도 표시 구간에 포함한다.
 * 별도 발화 작업이나 큐가 없어 프레임이 건너뛰어도 현재 시각에 맞는 말풍선만 그린다.
 */
internal fun chatter(time: Float, emit: (Speech) -> Unit) {
    if (time >= firstSpeech.start && time < firstSpeech.start + firstSpeech.duration + .28f) emit(firstSpeech)
    if (time >= secondSpeech.start && time < secondSpeech.start + secondSpeech.duration + .28f) emit(secondSpeech)
    if (time >= 6.55f) {
        val index = ((time - 6.55f) / 2.8f).toInt()
        val start = 6.55f + index * 2.8f
        if (time - start < 2.28f) emit(Speech(speakers[index % speakers.size], start, 2f))
    }
}

internal data class BubblePose(
    val scaleX: Float, val scaleY: Float, val rotation: Float, val offset: Float, val alpha: Float,
)

private data class BubbleKey(val at: Float, val x: Float, val y: Float, val rotation: Float)
private val bubbleKeys = listOf(
    BubbleKey(0f, .20f, .30f, -10f), BubbleKey(.20f, 1.10f, .90f, 4f),
    BubbleKey(.32f, .95f, 1.06f, -2f), BubbleKey(.46f, 1f, 1f, 0f),
)

/**
 * 두 종류의 말풍선에 공통으로 쓰는 포즈다. [age]는 발화부터의 초다. 등장 중에는 가로·세로 비율과
 * 회전을 키프레임에서 구하고, 부유 중에는 위아래로 2만큼 움직인다. 퇴장 중에는 위로 이동하면서
 * 크기와 불투명도를 낮춘다.
 */
internal fun bubblePose(age: Float, duration: Float): BubblePose {
    val phase = age.coerceAtLeast(0f) % 1.6f
    val float = -2f * easeInOut.transform(1f - kotlin.math.abs(phase / .8f - 1f))
    if (age >= duration) {
        val exit = easeIn.transform(progress(age, duration, .28f))
        val scale = 1f - .45f * exit
        return BubblePose(scale, scale, 6f * exit, float - 8f * exit, 1f - exit)
    }
    val index = (bubbleKeys.indexOfFirst { it.at >= age.coerceIn(0f, .46f) } - 1).coerceAtLeast(0)
    val from = bubbleKeys[index]
    val to = bubbleKeys[index + 1]
    val eased = easeInOut.transform(progress(age, from.at, to.at - from.at))
    return BubblePose(
        mix(from.x, to.x, eased), mix(from.y, to.y, eased),
        mix(from.rotation, to.rotation, eased), float, progress(age, 0f, .20f),
    )
}

// 점마다 0.09초 간격으로 나타나고 최대 크기 130%를 거쳐 기본 크기에 정착한다.
internal fun dotScale(age: Float, index: Int): Float {
    val elapsed = age - .20f - .09f * index
    return if (elapsed < .16f) mix(0f, 1.3f, progress(elapsed, 0f, .16f))
    else mix(1.3f, 1f, progress(elapsed, .16f, .10f))
}

// 1.05초 주기에서 각 점의 위상 차이를 구한다. 그리기 단계에서 높이와 불투명도에 함께 사용한다.
internal fun dotWave(age: Float, index: Int): Float {
    val cycle = age.coerceAtLeast(0f) % 1.05f - .14f * index
    return min(progress(cycle, 0f, .26f), 1f - progress(cycle, .26f, .26f))
}
