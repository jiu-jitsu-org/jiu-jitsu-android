package com.kyu.jiu_jitsu.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.kyu.jiu_jitsu.ui.SplashViewModel
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.ui.theme.Red500

@Composable
fun SplashScreen(
    enterLoginScreen: () -> Unit,
    enterHomeScreen: () -> Unit,
) {
    val viewModel = hiltViewModel<SplashViewModel>()

    LaunchedEffect(Unit) {
        viewModel.startFirstLogic()
    }

    LaunchedEffect(viewModel.splashUiState) {
        val uiState = viewModel.splashUiState
        when(uiState) {
            is UiState.Success -> {
                Log.d("@@@@@@@", "SplashScreen splashUiState.Success")
                val (bootStrapInfo, autoLogin) = uiState.result

                bootStrapInfo?.let { info ->
                    info.appVersionInfo?.let { appVersion ->
                        // TODO Check AppVersion
                    }

                    if (autoLogin) {
                        viewModel.tryAutoLogin()
                    } else {
                        enterLoginScreen()
                    }
                }
            }
            is UiState.Error -> {
                Log.d("@@@@@@@", "SplashScreen splashUiState.Error")
            }
            is UiState.Loading -> {
                Log.d("@@@@@@@", "SplashScreen splashUiState.Loading")
            }
            else -> {}
        }
    }

    LaunchedEffect(viewModel.autoLoginState) {
        val uiState = viewModel.autoLoginState
        when(uiState) {
            is UiState.Success -> {
                Log.d("@@@@@@@", "SplashScreen autoLoginState.Success")
                enterHomeScreen()
            }
            is UiState.Error -> {
                Log.d("@@@@@@@", "SplashScreen autoLoginState.Error")
            }
            is UiState.Loading -> {
                Log.d("@@@@@@@", "SplashScreen autoLoginState.Loading")
            }
            else -> {}
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Red500,
    ) {

    }

}