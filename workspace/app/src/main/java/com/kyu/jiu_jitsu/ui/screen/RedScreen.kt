package com.kyu.jiu_jitsu.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kyu.jiu_jitsu.ui.theme.Red500

@Composable
fun RedScreen(
    onLoginClick: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Red500,
    ) {
        Column {
            TextButton(
                onClick = onLoginClick
            ) {
                Text(text = "Login Screen")
            }
        }
    }
}