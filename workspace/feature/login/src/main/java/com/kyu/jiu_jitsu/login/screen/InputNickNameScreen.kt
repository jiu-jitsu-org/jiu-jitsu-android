package com.kyu.jiu_jitsu.login.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.kyu.jiu_jitsu.login.LoginViewModel
import com.kyu.jiu_jitsu.ui.theme.White

@Composable
fun InputNickNameScreen(
    modifier: Modifier
) {
    val viewModel = hiltViewModel<LoginViewModel>()

    Surface(
        modifier = modifier,
        color = White
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Text("InputNickNameScreen")
        }
    }
}