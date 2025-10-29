package com.kyu.jiu_jitsu.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.ui.theme.Blue100

@Composable
fun BlueScreen(
    goProfile: () -> Unit = {},
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Blue100,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = goProfile
            ) {
                Text("Profile Screen")
            }
        }
    }

}