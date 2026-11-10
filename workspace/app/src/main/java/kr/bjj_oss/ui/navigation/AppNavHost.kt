package kr.bjj_oss.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import kr.bjj_oss.login.screen.LoginScreen
import kr.bjj_oss.nickname.screen.NickNameScreen
import kr.bjj_oss.profile.screen.ModifyAcademyScreen
import kr.bjj_oss.profile.screen.ModifyProfileScreen
import kr.bjj_oss.profile.screen.ProfileScreen
import kr.bjj_oss.profile.screen.ModifyCompetitionScreen
import kr.bjj_oss.profile.screen.ModifyMyStyleScreen
import kr.bjj_oss.BuildConfig
import kr.bjj_oss.setting.SettingRoute
import kr.bjj_oss.setting.NotificationRoute
import kr.bjj_oss.setting.NotificationDestination
import kr.bjj_oss.web.WebContentRoute
import kr.bjj_oss.ui.screen.SplashScreen
import kr.bjj_oss.ui.routes.SettingScreen
import kr.bjj_oss.ui.routes.HomeGraph
import kr.bjj_oss.ui.routes.LoginGraph
import kr.bjj_oss.ui.routes.LoginScreen
import kr.bjj_oss.ui.routes.ModifyAcademyScreen
import kr.bjj_oss.ui.routes.ModifyCompetitionScreen
import kr.bjj_oss.ui.routes.ModifyMyStyleScreen
import kr.bjj_oss.ui.routes.ModifyProfileScreen
import kr.bjj_oss.ui.routes.NickNameScreen
import kr.bjj_oss.ui.routes.ProfileScreen
import kr.bjj_oss.ui.routes.HomeScreen
import kr.bjj_oss.ui.routes.SplashScreen

@Composable
fun AppNavHost(
    nav: NavHostController,
    modifier: Modifier,
    padding: PaddingValues,
    onWebFullscreenChanged: (Boolean) -> Unit,
    onStartupAuthenticated: () -> Unit,
) {
    NavHost(
        navController = nav,
        startDestination = SplashScreen,
        modifier = modifier,
    ) {
        // Belt Crowd가 시작 화면의 브랜딩에서 이어지므로 스플래시에는 기본 퇴장 페이드를 적용하지 않는다.
        composable<SplashScreen>(exitTransition = { ExitTransition.None }) {
            SplashScreen(
                enterLoginScreen = {
                    nav.navigate(LoginGraph) {
                        popUpTo(SplashScreen) { inclusive = true }

                        launchSingleTop = true
                        restoreState = true
                    }
                },
                enterHomeScreen = {
                    // 시작 화면이 현재 목적지일 때만 인사를 예약한다. 홈은 즉시 구성하고
                    // 화면 전체를 덮는 인사 전환은 앱 루트가 맡는다.
                    if (nav.currentDestination?.hasRoute(SplashScreen::class) == true) {
                        onStartupAuthenticated()
                        nav.navigate(HomeGraph) {
                            popUpTo(SplashScreen) { inclusive = true }

                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
        // Home
        navigation<HomeGraph>(startDestination = HomeScreen) {
            composable<ProfileScreen> { backStackEntry ->
                ProfileScreen(
                    modifier = modifier,
                    padding = padding,
                    onModifyClick = {
                        nav.navigate(ModifyProfileScreen)
                    },
                    savedStateHandle = backStackEntry.savedStateHandle,
                    onAcademyClick = { academyName ->
                        nav.navigate(ModifyAcademyScreen(academyName))
                    },
                    onMyStyleClick = { screenName ->
                        nav.navigate(ModifyMyStyleScreen(screenName))
                    },
                    onCompetitionClick = {
                        nav.navigate(ModifyCompetitionScreen)
                    }
                )
            }

            composable<HomeScreen>(
                enterTransition = {
                    // 스플래시에서 왔을 때는 앱 루트의 홈 슬라이드와 NavHost 전환을 겹치지 않는다.
                    if (initialState.destination.hasRoute(SplashScreen::class)) {
                        EnterTransition.None
                    } else null
                },
            ) {
                WebContentRoute(
                    modifier = Modifier.fillMaxSize(),
                    padding = padding,
                    onFullscreenChanged = onWebFullscreenChanged,
                    loginContent = { done -> WebLoginFlow(padding, done) },
                    // TODO: 카테고리의 실제 상대 주소를 입력하세요: categoryPath = "/실제-경로"
                    // 새 경로는 core:webview의 WebUrlPolicy와 계약 테스트에도 함께 등록해야 합니다.
                    // 연결 전에는 카테고리 탭을 비활성화합니다. 피드는 기존 루트 "/"를 사용합니다.
                    categoryPath = "/service-info",
                    // 같은-origin 상대 경로만 전달한다. 실제 origin과 허용 경로는 웹 host가 검증한다.
                    notificationPath = BuildConfig.COMMUNITY_NOTIFICATION_PATH,
                    myCommunityPath = BuildConfig.COMMUNITY_ME_PATH,
                )
            }

            composable<SettingScreen> {
                // 기존 설정 목적지를 설정 feature의 화면 진입점에 연결한다.
                SettingRoute(
                    versionName = BuildConfig.VERSION_NAME,
                    onBack = { nav.popBackStack() },
                    onLogin = { nav.navigate(LoginGraph) { launchSingleTop = true } },
                    onNotifications = { nav.navigate(NotificationDestination) { launchSingleTop = true } },
                    modifier = Modifier.fillMaxSize(),
                    padding = padding,
                )
            }
        }
        // A detail destination outside HomeGraph hides the bottom tabs and preserves Settings below it.
        composable<NotificationDestination> {
            NotificationRoute(
                onBack = { nav.popBackStack() },
                modifier = Modifier.fillMaxSize(),
                padding = padding,
            )
        }
        // Login
        navigation<LoginGraph>(startDestination = LoginScreen) {
            composable<LoginScreen>(
                enterTransition = {
                    // 스플래시와 같은 시작 배치에서 로그인 자체의 인트로가 이어진다.
                    if (initialState.destination.hasRoute(SplashScreen::class)) EnterTransition.None else null
                },
            ) {
                LoginScreen(
                    // 독립 로그인 진입점에서 사용한다. 웹 문서 위의 로그인은 기본 화면으로 구성한다.
                    showBeltCrowd = true,
                    serviceTermsContent = { type, onClose -> ServiceTermsDialog(type, onClose) },
                    modifier = modifier,
                    goHome = {
                        nav.navigate(HomeGraph) {
                            popUpTo(LoginGraph) { inclusive = true }

                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    goInputNickName = { isMarketingAgreed ->
                        nav.navigate(NickNameScreen(isMarketingAgreed)) {
                            popUpTo(LoginGraph) { inclusive = true }

                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        }
        // NickName
        composable<NickNameScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<NickNameScreen>()
            NickNameScreen(
                modifier = modifier,
                padding = padding,
                isMarketingAgree = args.isMarketingAgreed,
                goHome = {
                    nav.navigate(HomeGraph) {
                        popUpTo(NickNameScreen(args.isMarketingAgreed)) { inclusive = true }

                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        // Modify Profile
        composable<ModifyProfileScreen> { backStackEntry ->
            ModifyProfileScreen(
                modifier = modifier,
                padding = padding,
                savedStateHandle = backStackEntry.savedStateHandle,
                onAcademyClick = { academyName ->
                    nav.navigate(ModifyAcademyScreen(academyName))
                },
                onMyStyleClick = { screenName ->
                    nav.navigate(ModifyMyStyleScreen(screenName))
                },
                onBackClick = { nav.popBackStack() },
            )
        }
        // Modify Profile Academy
        composable<ModifyAcademyScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<ModifyAcademyScreen>()
            ModifyAcademyScreen(
                modifier = modifier,
                padding = padding,
                academyName = args.academyName,
                onCompleted = {
                    nav.previousBackStackEntry?.savedStateHandle?.set("isCompetitionUpdated", "AcademyName")
                    nav.popBackStack()
                },
                onBackClick = {
                    nav.popBackStack()
                }
            )
        }
        // Modify Competition
        composable<ModifyCompetitionScreen> {
            ModifyCompetitionScreen(
                modifier = modifier,
                padding = padding,
                onCompleteClick = {
                    nav.previousBackStackEntry?.savedStateHandle?.set("isCompetitionUpdated", "Competition")
                    nav.popBackStack()
                },
                onBackClick = {
                    nav.popBackStack()
                }
            )
        }
        // Modify My Style
        composable<ModifyMyStyleScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<ModifyMyStyleScreen>()
            ModifyMyStyleScreen(
                modifier = modifier,
                padding = padding,
                type = args.styleType,
                onCompleteClick = {
                    nav.previousBackStackEntry?.savedStateHandle?.set("isCompetitionUpdated", "MyStyle")
                    nav.popBackStack()
                },
                onBackClick = {
                    nav.popBackStack()
                }
            )
        }

    }
}
