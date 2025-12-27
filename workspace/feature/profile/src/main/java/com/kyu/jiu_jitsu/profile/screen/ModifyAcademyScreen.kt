package com.kyu.jiu_jitsu.profile.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.profile.ProfileViewModel
import com.kyu.jiu_jitsu.profile.R
import com.kyu.jiu_jitsu.ui.components.button.PrimaryCTAButton
import com.kyu.jiu_jitsu.ui.components.textfield.TransparentOutlinedTextField
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.White

/**
 * 도장 정보 수정
 * @param modifier Modifier
 * @param padding PaddingValues
 * @param academyName 도장 이름
 * @param onCompleteClick 완료
 * @param onBackClick 뒤로 가기
 */
@Composable
fun ModifyAcademyScreen(
    modifier: Modifier,
    padding: PaddingValues,
    academyName: String = "",
    onCompleteClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel = hiltViewModel<ProfileViewModel>()

    /** 작성 중인 */
    var strAcademy by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // TODO fetch User Profile data
        strAcademy = academyName
    }

    LaunchedEffect(viewModel.profileUiState) {
        val uiState = viewModel.profileUiState
        when(uiState) {
            is UiState.Success -> {
                onCompleteClick()
            }
            is UiState.Error -> {

            }
            is UiState.Loading -> {

            }
            else -> {}
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = White,
    ) {
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            // 최상단 앱바
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .paint(
                            painter = painterResource(com.kyu.jiu_jitsu.ui.R.drawable.ic_blue_left_back_button),
                            contentScale = ContentScale.Crop,
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true),
                            role = Role.Button,
                            onClick = onBackClick
                        )
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.profile_input_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = ColorComponents.Header.Header.Text,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(36.dp))
            }

            Spacer(modifier = Modifier.height(72.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.profile_hint_academy),
                style = MaterialTheme.typography.displayLarge,
                color = ColorComponents.TextFieldDisplay.Focus.Title,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            TransparentOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = strAcademy,
                placeholder = "",
                errorTextColor = ColorComponents.TextFieldDisplay.Default.Placeholder,
                onValueChange = { new ->
                    strAcademy = new
                },
            )

            // Bottom Button
            PrimaryCTAButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(51.dp),
                text = stringResource(com.kyu.jiu_jitsu.ui.R.string.common_complete),
                onClick = {
                    viewModel.changeAcademyName(strAcademy)
                },
                enabled = strAcademy.isNotEmpty()
            )

        }
    }
}