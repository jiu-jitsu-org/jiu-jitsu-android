package com.kyu.jiu_jitsu.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kyu.jiu_jitsu.login.screen.LoginScreen
import com.kyu.jiu_jitsu.nickname.screen.NickNameScreen
import com.kyu.jiu_jitsu.profile.screen.ModifyProfileScreen
import com.kyu.jiu_jitsu.profile.screen.ProfileScreen
import com.kyu.jiu_jitsu.ui.screen.BlueScreen
import com.kyu.jiu_jitsu.ui.screen.GrayScreen
import com.kyu.jiu_jitsu.ui.screen.RedScreen
import com.kyu.jiu_jitsu.ui.screen.SplashScreen
import com.kyu.jiu_jitsu.ui.routes.BlueScreen
import com.kyu.jiu_jitsu.ui.routes.GrayScreen
import com.kyu.jiu_jitsu.ui.routes.HomeGraph
import com.kyu.jiu_jitsu.ui.routes.LoginGraph
import com.kyu.jiu_jitsu.ui.routes.LoginScreen
import com.kyu.jiu_jitsu.ui.routes.ModifyProfileScreen
import com.kyu.jiu_jitsu.ui.routes.NickNameScreen
import com.kyu.jiu_jitsu.ui.routes.ProfileGraph
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
            composable<RedScreen> {
                RedScreen(
                    onLoginClick = { nav.navigate(LoginGraph) }
                )
            }

            composable<BlueScreen> {
                BlueScreen(
                    goProfile = { nav.navigate(ProfileGraph) }
                )
            }

            composable<GrayScreen> {
                GrayScreen()
            }
        }
        // Login
        navigation<LoginGraph>(startDestination = LoginScreen) {
            composable<LoginScreen>() {
                LoginScreen(
                    modifier = modifier,
                    goHome = {
                        nav.navigate(HomeGraph) {
                            popUpTo(LoginGraph) { inclusive = true }

                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    goInputNickName = {
                        nav.navigate(NickNameScreen)
                    }
                )
            }
        }
        // NickName
        composable<NickNameScreen> {
            NickNameScreen(
                modifier = modifier,
                padding = padding,
                goHome = {
                    nav.navigate(HomeGraph) {
                        popUpTo(NickNameScreen) { inclusive = true }

                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        // Profile
        navigation<ProfileGraph>(startDestination = ProfileScreen) {
            composable<ProfileScreen> {
                ProfileScreen(
                    modifier = modifier,
                    onModifyClick = {
                        nav.navigate(ModifyProfileScreen)
                    }
                )
            }
            composable<ModifyProfileScreen> {
                ModifyProfileScreen(
                    modifier = modifier,
                    padding = padding,
                )
            }
        }

    }
}