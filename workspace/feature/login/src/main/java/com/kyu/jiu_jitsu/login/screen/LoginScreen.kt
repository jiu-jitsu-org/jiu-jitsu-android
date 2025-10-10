package com.kyu.jiu_jitsu.login.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kyu.jiu_jitsu.login.LoginType
import com.kyu.jiu_jitsu.login.LoginViewModel
import com.kyu.jiu_jitsu.login.R
import com.kyu.jiu_jitsu.login.components.SignUpBottomSheet
import com.kyu.jiu_jitsu.ui.components.button.PressableButton
import com.kyu.jiu_jitsu.ui.components.button.TextButton
import com.kyu.jiu_jitsu.ui.theme.AppleBg
import com.kyu.jiu_jitsu.ui.theme.Color1F1F1F
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.CoolGray75
import com.kyu.jiu_jitsu.ui.theme.KakaoBg
import com.kyu.jiu_jitsu.ui.theme.White

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

    Surface(
        color = CoolGray75,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(35.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Kakao
            PressableButton(
                modifier = Modifier
                    .height(52.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.login_kakao),
                disabledBgColor = KakaoBg,
                enabledBgColor = KakaoBg,
                pressedBgColor = KakaoBg,
                enableTextColor = Color1F1F1F,
                pressedTextColor = Color1F1F1F,
                disabledTextColor = Color1F1F1F,
                roundedCorner = 12.dp,
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_login_kakao),
                        contentDescription = "LoginKakao"
                    )
                },
                onClick = {
                    open = true
                    viewModel.loginType = LoginType.KAKAO_ACCOUNT
                },
            )
            Spacer(modifier = Modifier.height(15.dp))
            // Google
            PressableButton(
                modifier = Modifier
                    .height(52.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.login_google),
                disabledBgColor = White,
                enabledBgColor = White,
                pressedBgColor = White,
                enableTextColor = Color1F1F1F,
                pressedTextColor = Color1F1F1F,
                disabledTextColor = Color1F1F1F,
                roundedCorner = 12.dp,
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_login_google),
                        contentDescription = "LoginGoogle"
                    )
                },
                onClick = {
                    open = true
                    viewModel.loginType = LoginType.GOOGLE
                },
            )
            Spacer(modifier = Modifier.height(15.dp))
            // Apple
            PressableButton(
                modifier = Modifier
                    .height(52.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.login_apple),
                disabledBgColor = AppleBg,
                enabledBgColor = AppleBg,
                pressedBgColor = AppleBg,
                enableTextColor = White,
                pressedTextColor = White,
                disabledTextColor = White,
                roundedCorner = 12.dp,
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_login_apple),
                        contentDescription = "LoginApple"
                    )
                },
                onClick = {
                    open = true
                    viewModel.loginType = LoginType.APPLE
                },
            )
            Spacer(modifier = Modifier.height(15.dp))
            // Skip
            TextButton(
                modifier = Modifier
                    .height(44.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.login_skip),
                pressedTextColor = White,
                enableTextColor = White,
                disabledTextColor = White,
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

                viewModel.startSnsLogin(context)

//                goInputNickName()
            }
        }
    }


}