package kr.bjj_oss.ui.belt

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.IntRect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import java.time.LocalDate
import kr.bjj_oss.R
import kotlin.random.Random

// 일요일부터 토요일까지의 화자다. 배열 값은 공용 장면의 갈색·보라·검정 캐릭터 인덱스를 가리킨다.
private val speakersByDay = intArrayOf(0, 4, 2, 0, 2, 4, 2)

/**
 * 기기의 현지 날짜로 요일을 정하고 요일별 문구 두 개 중 하나를 선택한다.
 * 리소스 배열의 인덱스 하나에 요일과 문구 선택을 함께 담아 화면 재생성 시 그대로 복원할 수 있다.
 */
internal fun selectBeltGreeting(): Int {
    val day = LocalDate.now().dayOfWeek.value % 7
    return day * 2 + Random.nextInt(2)
}

@Composable
internal fun beltGreeting(index: Int): BeltGreeting =
    BeltGreeting(speakersByDay[index / 2], stringArrayResource(R.array.belt_startup_greetings)[index])

/**
 * Activity에 해당하는 WindowManager 정보를 화면이 STARTED 상태일 때 수집한다.
 * 공용 장면에는 필요한 창 좌표와 방향만 전달한다. WindowManager의 타입과 생명주기는 앱에서 관리한다.
 */
@Composable
internal fun rememberWindowFold(activity: ComponentActivity?): State<BeltFold?> =
    produceState<BeltFold?>(null, activity) {
        if (activity == null) return@produceState
        val tracker = WindowInfoTracker.getOrCreate(activity)
        activity.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            tracker.windowLayoutInfo(activity).collect { info ->
                // 화면을 실제로 나누거나 콘텐츠를 가리는 접힘만 레이아웃 제약으로 사용한다.
                val fold = info.displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull {
                    it.isSeparating || it.occlusionType == FoldingFeature.OcclusionType.FULL
                }
                value = fold?.let {
                    val bounds = it.bounds
                    BeltFold(
                        IntRect(bounds.left, bounds.top, bounds.right, bounds.bottom),
                        it.orientation == FoldingFeature.Orientation.HORIZONTAL,
                    )
                }
            }
        }
    }
