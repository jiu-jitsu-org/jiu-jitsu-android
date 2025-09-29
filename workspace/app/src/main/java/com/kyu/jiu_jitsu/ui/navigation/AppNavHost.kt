package com.kyu.jiu_jitsu.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kyu.jiu_jitsu.login.screen.InputNickNameScreen
import com.kyu.jiu_jitsu.login.screen.LoginScreen
import com.kyu.jiu_jitsu.ui.BlueScreen
import com.kyu.jiu_jitsu.ui.GrayScreen
import com.kyu.jiu_jitsu.ui.RedScreen
import com.kyu.jiu_jitsu.ui.routes.BlueScreen
import com.kyu.jiu_jitsu.ui.routes.GrayScreen
import com.kyu.jiu_jitsu.ui.routes.HomeGraph
import com.kyu.jiu_jitsu.ui.routes.InputNickNameScreen
import com.kyu.jiu_jitsu.ui.routes.LoginGraph
import com.kyu.jiu_jitsu.ui.routes.LoginScreen
import com.kyu.jiu_jitsu.ui.routes.RedScreen

@Composable
fun AppNavHost(
    nav: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = nav,
        startDestination = LoginGraph,
        modifier = modifier,
    ) {
        navigation<HomeGraph>(startDestination = RedScreen) {
            composable<RedScreen> {
                RedScreen(
                    onLoginClick = { nav.navigate(LoginGraph) }
                )
            }

            composable<BlueScreen> {
                BlueScreen()
            }

            composable<GrayScreen> {
                GrayScreen()
            }
        }

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
                    goInputNickName = { nav.navigate(InputNickNameScreen) }
                )
            }

            composable<InputNickNameScreen> {
                InputNickNameScreen(
                    modifier = modifier,
                )
            }
        }

    }
}