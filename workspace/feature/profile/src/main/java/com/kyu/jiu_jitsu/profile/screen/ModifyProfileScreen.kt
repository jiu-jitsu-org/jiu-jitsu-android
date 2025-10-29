package com.kyu.jiu_jitsu.profile.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.kyu.jiu_jitsu.profile.ProfileViewModel
import com.kyu.jiu_jitsu.ui.theme.Blue500
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.White

@Composable
fun ModifyProfileScreen(
    modifier: Modifier,
    padding: PaddingValues,
) {
    val viewModel = hiltViewModel<ProfileViewModel>()

    LaunchedEffect(Unit) {
        // TODO fetch User Profile data
    }

    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = ColorComponents.List.Setting.Background,
    ) {

    }
}