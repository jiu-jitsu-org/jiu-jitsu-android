package kr.bjj_oss.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kr.bjj_oss.ui.SplashViewModel
import kr.bjj_oss.ui.state.UiState
import kr.bjj_oss.ui.theme.Red500

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
                // TODO: Render a retry action instead of leaving the splash surface static.
            }
            is UiState.Loading -> Unit
            else -> {}
        }
    }

    LaunchedEffect(viewModel.autoLoginState) {
        val uiState = viewModel.autoLoginState
        when(uiState) {
            is UiState.Success -> {
                enterHomeScreen()
            }
            is UiState.Error -> {
                if (
                    uiState.code == 401 ||
                    uiState.serverCode == "A0003" ||
                    uiState.serverCode == "U0002"
                ) {
                    enterLoginScreen()
                }
            }
            is UiState.Loading -> Unit
            else -> {}
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Red500,
    ) {

    }

}
