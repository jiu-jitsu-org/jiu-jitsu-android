package com.kyu.jiu_jitsu.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.kyu.jiu_jitsu.SplashViewModel
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.ui.theme.Red500

@Composable
fun SplashScreen(
    enterLoginScreen: () -> Unit,
    enterHomeScreen: () -> Unit,
) {
    val viewModel = hiltViewModel<SplashViewModel>()

    LaunchedEffect(Unit) {
        viewModel.startLogic()
    }

    LaunchedEffect(viewModel.splashUiState) {
        when(viewModel.splashUiState) {
            is UiState.Success -> {
                Log.d("@@@@@@@", "SplashScreen UIState.Success")
                enterLoginScreen()
            }
            is UiState.Error -> {
                Log.d("@@@@@@@", "SplashScreen UIState.Error")
            }
            is UiState.Loading -> {
                Log.d("@@@@@@@", "SplashScreen UIState.Loading")
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