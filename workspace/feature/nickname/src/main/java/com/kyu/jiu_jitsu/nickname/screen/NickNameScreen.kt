package com.kyu.jiu_jitsu.nickname.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.nickname.NickNameViewModel
import com.kyu.jiu_jitsu.nickname.R
import com.kyu.jiu_jitsu.ui.components.button.PrimaryButton
import com.kyu.jiu_jitsu.ui.components.button.PrimaryCTAButton
import com.kyu.jiu_jitsu.ui.components.textfield.TransparentOutlinedTextField
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.CoolGray25
import com.kyu.jiu_jitsu.ui.theme.White

sealed interface NickNameState {
    /** 닉네임 입력 상태 - 기본 */
    data object Idle : NickNameState
    /** 닉네임 입력 상태 - 유효성 검사 실패 */
    data object ValidationError : NickNameState
    /** 닉네임 입력 상태 - 유효성 검사 성공 */
    data object ValidationSuccess : NickNameState
    /** 닉네임 입력 상태 - 중복 검사 실패 */
    data object DuplicateError : NickNameState
    /** 닉네임 입력 상태 - 중복 검사 성공 */
    data object DuplicateSuccess : NickNameState
}

@Composable
fun NickNameScreen(
    modifier: Modifier,
    padding: PaddingValues,
    isShowBackArrowIcon: Boolean = false,
    goHome: () -> Unit = {},
) {
    val viewModel = hiltViewModel<NickNameViewModel>()

    /** 닉네임 선택 Info Title String ID */
    @StringRes
    var titleId by remember { mutableIntStateOf(R.string.nickname_title_default) }
    /** 작성 중인 닉네임 */
    var strNickName by remember { mutableStateOf("") }
    /** 하단 버튼 Title String ID*/
    @StringRes
    var textBottomBtn by remember { mutableIntStateOf(com.kyu.jiu_jitsu.ui.R.string.common_confirm) }
    /** 하단 버튼 사용 가능 여부 */
    var enableBottomBtn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getLocalNickName()
    }

    LaunchedEffect(viewModel.localNickNameState) {
        val uiState = viewModel.localNickNameState
        when (uiState) {
            is UiState.Success -> {

            }

            is UiState.Error -> {

            }

            is UiState.Loading -> {

            }

            else -> {}
        }
    }

    LaunchedEffect(viewModel.inputNickNameState) {
        val inputNickNameState = viewModel.inputNickNameState
        when (inputNickNameState) {
            is NickNameState.Idle -> {
                textBottomBtn = com.kyu.jiu_jitsu.ui.R.string.common_confirm
                titleId = R.string.nickname_title_default
            }
            is NickNameState.ValidationError -> {
                enableBottomBtn = false
                titleId = R.string.nickname_title_validate_error
            }
            is NickNameState.ValidationSuccess -> {

            }
            is NickNameState.DuplicateError -> {
                enableBottomBtn = false
                titleId = R.string.nickname_title_duplicate_error
            }
            is NickNameState.DuplicateSuccess -> {
                textBottomBtn = com.kyu.jiu_jitsu.ui.R.string.common_go_home
                titleId = R.string.nickname_title_duplicate_success
            }
        }
    }

    Surface(
        color = White,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 25.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(color = CoolGray25)
                )

                Text(
                    text = stringResource(titleId),
                    style = MaterialTheme.typography.displayLarge,
                    color = ColorComponents.TextFieldDisplay.Default.Title,
                    textAlign = TextAlign.Center
                )

                TransparentOutlinedTextField(
                    modifier = Modifier.wrapContentWidth(),
                    value = strNickName,
                    placeholder = stringResource(R.string.nickname_hint_default),
                    errorTextColor = ColorComponents.TextFieldDisplay.Default.Placeholder,
                    isError = viewModel.inputNickNameState is NickNameState.ValidationError
                            || viewModel.inputNickNameState is NickNameState.DuplicateError,
                    onValueChange = { new ->
                        strNickName = new
                        enableBottomBtn = new.trim().isNotBlank()
                        viewModel.inputNickNameState = NickNameState.Idle
                    },
                )
            }

            // Bottom Button
            PrimaryCTAButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(51.dp),
                text = stringResource(textBottomBtn),
                onClick = {
                    if (viewModel.inputNickNameState is NickNameState.DuplicateSuccess) {
                        goHome()
                    } else {
                        viewModel.onClickNickNameBottomBtn(strNickName)
                    }
                },
                enabled = enableBottomBtn
            )

        }
    }

}