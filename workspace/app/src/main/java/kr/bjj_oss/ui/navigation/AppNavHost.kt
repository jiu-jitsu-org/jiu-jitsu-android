package kr.bjj_oss.ui.navigation

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
) {
    NavHost(
        navController = nav,
        startDestination = SplashScreen,
        modifier = modifier,
    ) {
        // Splash
        composable<SplashScreen> {
            SplashScreen(
                enterLoginScreen = {
                    nav.navigate(LoginGraph) {
                        popUpTo(SplashScreen) { inclusive = true }

                        launchSingleTop = true
                        restoreState = true
                    }
                },
                enterHomeScreen = {
                    nav.navigate(HomeGraph) {
                        popUpTo(SplashScreen) { inclusive = true }

                        launchSingleTop = true
                        restoreState = true
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

            composable<HomeScreen> {
                WebContentRoute(
                    modifier = Modifier.fillMaxSize(),
                    padding = padding,
                    onFullscreenChanged = onWebFullscreenChanged,
                    loginContent = { done -> WebLoginFlow(padding, done) },
                )
            }

            composable<SettingScreen> {
                // 기존 설정 목적지를 설정 feature의 화면 진입점에 연결한다.
                SettingRoute(
                    versionName = BuildConfig.VERSION_NAME,
                    onBack = { nav.popBackStack() },
                    onLogin = { nav.navigate(LoginGraph) { launchSingleTop = true } },
                    modifier = Modifier.fillMaxSize(),
                    padding = padding,
                )
            }
        }
        // Login
        navigation<LoginGraph>(startDestination = LoginScreen) {
            composable<LoginScreen> {
                LoginScreen(
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
