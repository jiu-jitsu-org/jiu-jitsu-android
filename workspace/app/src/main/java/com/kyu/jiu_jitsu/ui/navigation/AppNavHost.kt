package com.kyu.jiu_jitsu.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.kyu.jiu_jitsu.login.screen.LoginScreen
import com.kyu.jiu_jitsu.nickname.screen.NickNameScreen
import com.kyu.jiu_jitsu.profile.screen.ModifyAcademyScreen
import com.kyu.jiu_jitsu.profile.screen.ModifyProfileScreen
import com.kyu.jiu_jitsu.profile.screen.ProfileScreen
import com.kyu.jiu_jitsu.profile.screen.ModifyCompetitionScreen
import com.kyu.jiu_jitsu.profile.screen.ModifyMyStyleScreen
import com.kyu.jiu_jitsu.ui.screen.GrayScreen
import com.kyu.jiu_jitsu.ui.screen.RedScreen
import com.kyu.jiu_jitsu.ui.screen.SplashScreen
import com.kyu.jiu_jitsu.ui.routes.GrayScreen
import com.kyu.jiu_jitsu.ui.routes.HomeGraph
import com.kyu.jiu_jitsu.ui.routes.LoginGraph
import com.kyu.jiu_jitsu.ui.routes.LoginScreen
import com.kyu.jiu_jitsu.ui.routes.ModifyAcademyScreen
import com.kyu.jiu_jitsu.ui.routes.ModifyCompetitionScreen
import com.kyu.jiu_jitsu.ui.routes.ModifyMyStyleScreen
import com.kyu.jiu_jitsu.ui.routes.ModifyProfileScreen
import com.kyu.jiu_jitsu.ui.routes.NickNameScreen
import com.kyu.jiu_jitsu.ui.routes.ProfileScreen
import com.kyu.jiu_jitsu.ui.routes.RedScreen
import com.kyu.jiu_jitsu.ui.routes.SplashScreen

@Composable
fun AppNavHost(
    nav: NavHostController,
    modifier: Modifier,
    padding: PaddingValues,
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
        navigation<HomeGraph>(startDestination = RedScreen) {
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

            composable<RedScreen> {
                RedScreen(
                    onLoginClick = { nav.navigate(LoginGraph) }
                )
            }

            composable<GrayScreen> {
                GrayScreen()
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
                onCompleteClick = {
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
