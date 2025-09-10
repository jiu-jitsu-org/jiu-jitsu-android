package com.kyu.jiu_jitsu.login

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.kyu.jiu_jitsu.ui.components.PressableButton

@Composable
fun LoginScreen() {
    val viewModel = hiltViewModel<LoginViewModel>()

    PressableButton(
        text = "TestPressable",
        onClick = {
            viewModel.getUser(10)
        },
    )
}