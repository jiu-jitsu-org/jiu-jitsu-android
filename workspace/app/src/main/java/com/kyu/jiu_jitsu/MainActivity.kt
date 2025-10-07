package com.kyu.jiu_jitsu

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kyu.jiu_jitsu.ui.components.MainBottomNavigationBar
import com.kyu.jiu_jitsu.ui.navigation.AppNavHost
import com.kyu.jiu_jitsu.ui.routes.BlueScreen
import com.kyu.jiu_jitsu.ui.routes.GrayScreen
import com.kyu.jiu_jitsu.ui.routes.HomeGraph
import com.kyu.jiu_jitsu.ui.routes.LoginGraph
import com.kyu.jiu_jitsu.ui.routes.RedScreen
import com.kyu.jiu_jitsu.ui.theme.JiuJitsuPjtTheme
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
    val mainBottomNavItems = listOf(RedScreen, BlueScreen, GrayScreen)

    val bottomBarDestinations = remember { setOf(HomeGraph::class) }
    val backStack by navController.currentBackStackEntryAsState()
    val destination = backStack?.destination

    val showBottomBar = destination?.hierarchy?.any { node ->
        bottomBarDestinations.any { route -> node.hasRoute(route) }
    } == true

    val edge = rememberEdgeBehavior(destination)

    // (선택) 아이콘 밝기 정책: 예시로 배경이 밝은 홈탭에 어두운 아이콘, 컨텐츠가 어두운 상세에 밝은 아이콘
    val statusIconsDark = when (edge) {
        EdgeBehavior.Extend        -> false // 배경 어두움 가정
        EdgeBehavior.PadStatusOnly -> true  // 배경 밝음 가정
        EdgeBehavior.PadSystemBars -> true
    }
    val navIconsDark = true
    EdgeToEdgeChrome(
        darkIconsOnStatusBar = statusIconsDark,
        darkIconsOnNavBar = navIconsDark
    )

    // 핵심: 화면별 insets를 다르게
    val contentInsets = when (edge) {
        EdgeBehavior.Extend        -> WindowInsets(0, 0, 0, 0)
        EdgeBehavior.PadStatusOnly -> WindowInsets.statusBars
        EdgeBehavior.PadSystemBars -> WindowInsets.systemBars
    }

    Scaffold(
        contentWindowInsets = contentInsets,
        bottomBar = {
            AnimatedVisibility(visible = showBottomBar) {
                MainBottomNavigationBar(
                    navHostController = navController,
                    navItems = mainBottomNavItems,
                )
            }
        }
    ) { innerPadding ->

        AppNavHost(
            nav = navController,
            modifier = Modifier
                .consumeWindowInsets(innerPadding)
                .fillMaxSize(),
        )
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
    val isFullScreen = dest?.hierarchy?.any { it.hasRoute(LoginGraph::class) } == true
    val isHomeTabs   = dest?.hierarchy?.any { it.hasRoute(HomeGraph::class) } == true

    return when {
        isFullScreen -> EdgeBehavior.Extend        // 상세 화면: 뒤로 배경이 보여도 OK
        isHomeTabs   -> EdgeBehavior.PadStatusOnly // 탭 루트들: 상단만 보호
        else         -> EdgeBehavior.PadSystemBars
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