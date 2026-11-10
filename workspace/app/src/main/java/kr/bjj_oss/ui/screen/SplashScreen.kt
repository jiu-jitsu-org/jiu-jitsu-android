package kr.bjj_oss.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kr.bjj_oss.ui.SplashViewModel
import kr.bjj_oss.ui.state.UiState
import kr.bjj_oss.ui.belt.BeltSplash

@Composable
fun SplashScreen(
    enterLoginScreen: () -> Unit,
    enterHomeScreen: () -> Unit,
) {
    val viewModel = hiltViewModel<SplashViewModel>()

    LaunchedEffect(Unit) {
        // 재생성 시 ViewModel에 남은 부트스트랩 결과를 재사용한다. 이전 컴포지션과 함께
        // 취소된 요청은 Loading일 수 있으므로 Idle과 함께 재시작 대상으로 둔다.
        if (viewModel.splashUiState is UiState.Idle || viewModel.splashUiState is UiState.Loading) {
            viewModel.startFirstLogic()
        }
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
                        // 인증이 이미 끝났다면 그 결과를 아래 전환 효과에서 처리한다.
                        if (viewModel.autoLoginState is UiState.Idle || viewModel.autoLoginState is UiState.Loading) {
                            viewModel.tryAutoLogin()
                        }
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

    // 부트스트랩과 자동 인증을 기다리는 동안에는 로그인 인트로와 같은 시작 구도를 유지한다.
    BeltSplash(Modifier.fillMaxSize())
}
