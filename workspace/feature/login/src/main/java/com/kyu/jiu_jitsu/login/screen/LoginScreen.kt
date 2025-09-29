package com.kyu.jiu_jitsu.login.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kyu.jiu_jitsu.login.LoginViewModel
import com.kyu.jiu_jitsu.login.R
import com.kyu.jiu_jitsu.login.components.SignUpBottomSheet
import com.kyu.jiu_jitsu.ui.components.button.DartCTAButton
import com.kyu.jiu_jitsu.ui.components.button.NeutralButton
import com.kyu.jiu_jitsu.ui.components.button.PrimaryButton
import com.kyu.jiu_jitsu.ui.components.button.TextButton
import com.kyu.jiu_jitsu.ui.components.button.TintButton
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.CoolGray75
import com.kyu.jiu_jitsu.ui.theme.Red500

sealed class LoginType(val type: String) {
    object KAKAO_ACCOUNT : LoginType("KAKAO_ACCOUNT")
    object GOOGLE : LoginType("GOOGLE")
    object APPLE : LoginType("APPLE")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier,
    goHome: () -> Unit,
    goInputNickName: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<LoginViewModel>()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var open by rememberSaveable { mutableStateOf(false) }
    var loginType by remember { mutableStateOf<LoginType?>(null) }

    Surface(
        color = CoolGray75,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Kakao
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Kakao",
                onClick = {
                    open = true
                    loginType = LoginType.KAKAO_ACCOUNT
                },
            )
            Spacer(modifier = Modifier.height(15.dp))
            // Google
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Google",
                onClick = {
                    open = true
                    loginType = LoginType.GOOGLE
                },
            )
            Spacer(modifier = Modifier.height(15.dp))
            // Apple
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Apple",
                onClick = {
                    open = true
                    loginType = LoginType.APPLE
                },
            )
            Spacer(modifier = Modifier.height(15.dp))
            // Skip
            TextButton(
                text = stringResource(R.string.login_skip),
                onClick = {
                    goHome()
                }
            )
            Spacer(modifier = Modifier.height(15.dp))
        }
    }

    if (open) {
        ModalBottomSheet(
            containerColor = ColorComponents.BottomSheet.Selected.Container.Background,
            contentColor = ColorComponents.BottomSheet.Selected.Container.Title,
            scrimColor = ColorComponents.BottomSheet.Selected.Container.Scrim,
            sheetState = sheetState,
            onDismissRequest = {
                open = false
                loginType = null
            },
            shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    width = 48.dp,
                    height = 4.dp,
                    color = ColorComponents.BottomSheet.Selected.Container.Handle
                )
            },
        ) {
            SignUpBottomSheet { agreeList ->
                Log.d("@@@@@@", "@@@@@@ $agreeList")
                open = false

                loginType?.let {
                    viewModel.startSnsLogin(it, context)
                }
//                goInputNickName()
            }
        }
    }


}