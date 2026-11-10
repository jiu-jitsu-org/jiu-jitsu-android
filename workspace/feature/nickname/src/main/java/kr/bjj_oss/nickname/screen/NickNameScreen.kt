package kr.bjj_oss.nickname.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.bjj_oss.nickname.*
import kr.bjj_oss.nickname.R
import kr.bjj_oss.nickname.model.NickNameState

@Composable
fun NickNameScreen(
    modifier: Modifier, padding: PaddingValues, isMarketingAgree: Boolean,
    isShowBackArrowIcon: Boolean = false, goHome: () -> Unit,
    initialNickname: String? = null, onBackClick: () -> Unit = {},
) {
    if (initialNickname != null) {
        val model = hiltViewModel<EditNicknameViewModel>()
        LaunchedEffect(initialNickname) { model.initialize(initialNickname) }
        val state by model.state.collectAsStateWithLifecycle()
        val context = LocalContext.current
        val complete by rememberUpdatedState(goHome)
        LaunchedEffect(model) {
            model.events.collect { changed ->
                if (changed) Toast.makeText(context, R.string.nickname_updated, Toast.LENGTH_SHORT)
                    .show()
                complete()
            }
        }
        NicknameForm(
            modifier,
            padding,
            stringResource(R.string.nickname_edit),
            state.input,
            model::input,
            state.error?.let { stringResource(it) }
                ?: if (state.checked == state.input) stringResource(R.string.nickname_title_duplicate_success) else stringResource(
                    R.string.nickname_hint_default
                ),
            state.error != null,
            state.busy,
            state.input.isNotBlank() && state.error != R.string.nickname_title_duplicate_error && state.error != R.string.nickname_title_validate_error,
            stringResource(R.string.nickname_edit),
            model::submit,
            onBackClick,
            inputEnabled = !state.busy || state.checked == null)
    } else {
        val model = hiltViewModel<NickNameViewModel>()
        val loading by model.loadingUiState.collectAsStateWithLifecycle()
        val submitting by model.submitting.collectAsStateWithLifecycle()
        val state by model.validateNickNameState.collectAsStateWithLifecycle()
        val local by model.localNickNameState.collectAsStateWithLifecycle()
        val error by model.errorUiState.collectAsStateWithLifecycle()
        var input by rememberSaveable { mutableStateOf<String?>(null) }
        val value = input ?: local.orEmpty()
        val feedback = stringResource(
            when (state) {
                NickNameState.ValidationSuccess -> R.string.nickname_title_duplicate_success
                NickNameState.ValidationError -> R.string.nickname_title_validate_error
                NickNameState.DuplicateError -> R.string.nickname_title_duplicate_error
                else -> if (error != null) R.string.nickname_network_error else R.string.nickname_hint_default
            }
        )
        NicknameForm(
            modifier,
            padding,
            stringResource(R.string.nickname_title_default),
            value,
            { input = it; model.onAction(NickNameAction.InitNickNameState) },
            feedback,
            state is NickNameState.ValidationError || state is NickNameState.DuplicateError || error != null,
            loading,
            value.isNotBlank() && state !is NickNameState.ValidationError && state !is NickNameState.DuplicateError,
            stringResource(if (state is NickNameState.Succeed) kr.bjj_oss.ui.R.string.common_go_home else kr.bjj_oss.ui.R.string.common_confirm),
            {
                when (state) {
                    NickNameState.Succeed -> goHome()
                    NickNameState.ValidationSuccess -> model.onAction(
                        NickNameAction.SignUp(
                            value,
                            isMarketingAgree
                        )
                    )

                    else -> model.onAction(NickNameAction.ValidateNickName(value))
                }
            },
            if (isShowBackArrowIcon) onBackClick else null,
            inputEnabled = !submitting
        )
    }
}

@Composable
private fun NicknameForm(
    modifier: Modifier,
    padding: PaddingValues,
    title: String,
    value: String,
    onInput: (String) -> Unit,
    feedback: String,
    error: Boolean,
    busy: Boolean,
    enabled: Boolean,
    button: String,
    onSubmit: () -> Unit,
    onBack: (() -> Unit)?,
    inputEnabled: Boolean = true
) {
    Surface(modifier.fillMaxSize(), color = androidx.compose.ui.graphics.Color.White) {
        Box(
            Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (onBack != null) TextButton(onClick = onBack) { Text(stringResource(R.string.nickname_back)) }
                Text(title, style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(
                    value,
                    onInput,
                    Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = error,
                    enabled = inputEnabled,
                    label = { Text(stringResource(R.string.nickname_hint_default)) })
                Text(
                    feedback,
                    color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = onSubmit,
                    enabled = enabled && !busy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 51.dp),
                    contentPadding = PaddingValues(16.dp)
                ) { Text(button) }
                if (busy) CircularProgressIndicator()
            }
        }
    }
}
