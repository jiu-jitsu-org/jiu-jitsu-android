package kr.bjj_oss

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kr.bjj_oss.ui.belt.BeltGreetingDuration
import kr.bjj_oss.ui.belt.BeltGreetingScene
import kr.bjj_oss.ui.belt.LocalBeltFold
import kr.bjj_oss.ui.belt.beltGreeting
import kr.bjj_oss.ui.belt.beltHomeProgress
import kr.bjj_oss.ui.belt.rememberBeltMotionEnabled
import kr.bjj_oss.ui.belt.rememberBeltTime
import kr.bjj_oss.ui.belt.rememberWindowFold
import kr.bjj_oss.ui.belt.selectBeltGreeting
import kr.bjj_oss.ui.insets.StatusBarBackground
import kr.bjj_oss.ui.components.MainBottomNavigationBar
import kr.bjj_oss.ui.navigation.AppNavHost
import kr.bjj_oss.ui.routes.SettingScreen
import kr.bjj_oss.ui.routes.HomeGraph
import kr.bjj_oss.ui.routes.LoginGraph
import kr.bjj_oss.ui.routes.SplashScreen
import kr.bjj_oss.ui.routes.ProfileGraph
import kr.bjj_oss.ui.routes.ProfileScreen
import kr.bjj_oss.ui.routes.ModifyProfileScreen
import kr.bjj_oss.ui.routes.HomeScreen
import kr.bjj_oss.ui.theme.JiuJitsuPjtTheme
import kr.bjj_oss.ui.theme.White
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JiuJitsuPjtTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val fold by rememberWindowFold(LocalContext.current.findActivity() as? ComponentActivity)
    val motion = rememberBeltMotionEnabled()
    // 인사가 없는 상태는 -1이다. 유효한 인덱스에는 화자와 문구 선택이 모두 들어 있어
    // 회전이나 접힘으로 Activity가 다시 만들어져도 인사 내용을 다시 고를 필요가 없다.
    var greetingIndex by rememberSaveable { mutableIntStateOf(-1) }
    val startGreeting: () -> Unit = remember(motion) {
        { if (motion.value && greetingIndex < 0) greetingIndex = selectBeltGreeting() }
    }
    val greetingTime = if (greetingIndex >= 0 && motion.value) {
        rememberBeltTime(enabled = true, limit = BeltGreetingDuration)
    } else null
    // 앱 루트에서는 완료 여부만 읽고, 프레임 값은 장면의 Canvas와 홈 레이어에서 읽는다.
    // 완료되거나 도중에 모션이 꺼지면 인사 상태를 비워 홈을 바로 사용할 수 있게 한다.
    val greetingFinished by remember(greetingTime) {
        derivedStateOf { greetingTime == null || greetingTime.value >= BeltGreetingDuration }
    }
    LaunchedEffect(greetingFinished, greetingIndex) {
        if (greetingFinished && greetingIndex >= 0) greetingIndex = -1
    }
    val greetingVisible = greetingTime != null && !greetingFinished
    // 홈을 먼저 구성하므로 인사가 보이는 동안에는 뒤로가기가 그 홈에 전달되지 않게 한다.
    BackHandler(enabled = greetingVisible) {}
    var webFullscreen by remember { mutableStateOf(false) }
    val mainBottomNavItems = listOf(HomeScreen, ProfileScreen, SettingScreen)

    val bottomBarDestinations = remember { setOf(HomeGraph::class) }
    val backStack by navController.currentBackStackEntryAsState()
    val destination = backStack?.destination

    val showBottomBar = destination?.hierarchy?.any { node ->
        bottomBarDestinations.any { route -> node.hasRoute(route) }
    } == true && !webFullscreen

    var profileDarkIcons by remember { mutableStateOf(false) }
    val isProfile = destination?.hasRoute(ProfileScreen::class) == true || destination?.hasRoute(
        ModifyProfileScreen::class
    ) == true
    val destinationEdge = rememberEdgeBehavior(destination)
    val edge = if (webFullscreen) EdgeBehavior.PadSystemBars else destinationEdge

    // (선택) 아이콘 밝기 정책: 예시로 배경이 밝은 홈탭에 어두운 아이콘, 컨텐츠가 어두운 상세에 밝은 아이콘
    val statusIconsDark = !greetingVisible && if (isProfile) profileDarkIcons else when (edge) {
        EdgeBehavior.Extend -> false // 배경 어두움 가정
        EdgeBehavior.PadStatusOnly -> true  // 배경 밝음 가정
        EdgeBehavior.PadSystemBars -> true
    }
    val navIconsDark = !greetingVisible && destination?.hasRoute(SplashScreen::class) != true
    EdgeToEdgeChrome(
        darkIconsOnStatusBar = statusIconsDark,
        darkIconsOnNavBar = navIconsDark
    )

    // 핵심: 화면별 insets를 다르게
    val contentInsets = when (edge) {
        EdgeBehavior.Extend -> WindowInsets(0, 0, 0, 0)
        EdgeBehavior.PadStatusOnly -> WindowInsets.statusBars
        EdgeBehavior.PadSystemBars -> WindowInsets.systemBars
    }

    // 시스템 바는 app만 소유한다. 투명 상태바 뒤에 흰 배경을 그리되, 실제 상태바 높이는
    // 런타임 inset으로 계산한다. 이 배경은 padding을 추가하지 않으므로 Scaffold에서 전달한
    // systemBars inset과 중복되지 않는다. 마지막 서브/로그인을 닫으면 기존 화면 색으로 복귀한다.
    // 앱에서 관찰한 힌지를 시작·로그인·인사에 공유한다. 각 장면이 자신의 제약으로 안전 영역을 결정한다.
    CompositionLocalProvider(LocalBeltFold provides fold) {
        Box(Modifier.fillMaxSize()) {
            if (greetingVisible && greetingTime != null) {
                BeltGreetingScene(beltGreeting(greetingIndex), greetingTime, Modifier.fillMaxSize())
            }
            val homeLayer = if (greetingVisible && greetingTime != null) {
                // 실제 홈과 하단 탭을 함께 올린다. 창 높이가 바뀌어도 진행률은 유지하고 이동 거리만
                // 새 높이에 맞춘다. 전환 중에는 홈의 접근성 노출을 제한한다.
                Modifier
                    .clearAndSetSemantics {}
                    .graphicsLayer {
                        val progress = beltHomeProgress(greetingTime.value)
                        translationY = size.height * (1f - progress)
                        val radius = (28f * (1f - progress)).dp
                        shape = RoundedCornerShape(topStart = radius, topEnd = radius)
                        clip = true
                    }
            } else Modifier
            Scaffold(
                modifier = homeLayer,
                contentWindowInsets = contentInsets,
                bottomBar = {
                    AnimatedVisibility(
                        visible = showBottomBar,
                        enter = fadeIn(),
                    ) {
                        Surface(
                            color = White,
                            shadowElevation = 10.dp
                        ) {
                            MainBottomNavigationBar(
                                navHostController = navController,
                                navItems = mainBottomNavItems,
                            )
                        }
                    }
                }
            ) { innerPadding ->
                AppNavHost(
                    nav = navController,
                    modifier = Modifier
                        .fillMaxSize()
                        .consumeWindowInsets(innerPadding),
                    padding = innerPadding,
                    onWebFullscreenChanged = { webFullscreen = it },
                    onStartupAuthenticated = startGreeting,
                    onProfileHeaderAppearanceChanged = { profileDarkIcons = it },
                )
            }
            if (webFullscreen && !greetingVisible) StatusBarBackground(color = Color.White)
            if (greetingVisible) {
                // 전환이 끝나기 전에 드러난 홈에 터치가 닿지 않도록 별도 입력 레이어에서 소비한다.
                Box(Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) awaitPointerEvent().changes.forEach { it.consume() }
                        }
                    })
            }
        }
    }
}

// 1) 화면별 E2E 정책
sealed interface EdgeBehavior {
    data object Extend : EdgeBehavior             // 상태바까지 확장 (패딩 없음)
    data object PadStatusOnly : EdgeBehavior      // 상태바 영역만 보호
    data object PadSystemBars : EdgeBehavior      // 상/하 시스템바 모두 보호
}

// 2) 현재 destination을 정책으로 매핑
@Composable
private fun rememberEdgeBehavior(dest: NavDestination?): EdgeBehavior {
    val isFullScreen = dest?.hierarchy?.any {
        it.hasRoute(LoginGraph::class) || it.hasRoute(ProfileGraph::class) || it.hasRoute(
            SplashScreen::class
        )
    } == true
    val isHomeTabs = dest?.hierarchy?.any { it.hasRoute(HomeGraph::class) } == true

    return when {
        dest?.hasRoute(ProfileScreen::class) == true || dest?.hasRoute(ModifyProfileScreen::class) == true -> EdgeBehavior.Extend
        isFullScreen -> EdgeBehavior.Extend        // 상세 화면: 뒤로 배경이 보여도 OK
        isHomeTabs -> EdgeBehavior.PadStatusOnly // 탭 루트들: 상단만 보호
        else -> EdgeBehavior.PadSystemBars
    }
}

// 3) 상태바/네비바 아이콘 대비 및 스크림도 화면별로 조정 (선택)
@Composable
private fun EdgeToEdgeChrome(
    darkIconsOnStatusBar: Boolean,
    darkIconsOnNavBar: Boolean,
    statusScrim: Color = Color.Transparent,
    navScrim: Color = Color.Transparent,
) {
    val activity = LocalContext.current.findActivity() as? ComponentActivity ?: return
    LaunchedEffect(darkIconsOnStatusBar, darkIconsOnNavBar, statusScrim, navScrim) {
        activity.enableEdgeToEdge(
            statusBarStyle =
                if (darkIconsOnStatusBar)
                    SystemBarStyle.light(statusScrim.toArgb(), statusScrim.toArgb())
                else
                    SystemBarStyle.dark(statusScrim.toArgb()),
            navigationBarStyle =
                if (darkIconsOnNavBar)
                    SystemBarStyle.light(navScrim.toArgb(), navScrim.toArgb())
                else
                    SystemBarStyle.dark(navScrim.toArgb())
        )
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
