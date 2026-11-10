package kr.bjj_oss.profile.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.bjj_oss.profile.R
import kr.bjj_oss.profile.components.*
import kr.bjj_oss.profile.viewmodel.*

@Composable
fun ModifyAcademyScreen(
    modifier: Modifier,
    padding: PaddingValues,
    academyName: String = "",
    onCompleted: () -> Unit,
    onBackClick: () -> Unit
) {
    val model = hiltViewModel<ModifyAcademyViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    var name by rememberSaveable { mutableStateOf(academyName) }
    val context = LocalContext.current
    val complete by rememberUpdatedState(onCompleted)
    LaunchedEffect(model) {
        model.events.collect {
            Toast.makeText(
                context,
                it.message,
                Toast.LENGTH_SHORT
            ).show(); complete()
        }
    }
    ProfileForm(
        stringResource(if (academyName.isBlank()) R.string.profile_input_title else R.string.profile_academy_edit),
        padding,
        onBackClick,
        modifier
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.profile_hint_academy)) },
            singleLine = true,
            enabled = !state.saving,
            isError = name.isNotEmpty() && !isValidAcademy(name)
        )
        if (name.isNotEmpty() && !isValidAcademy(name)) Text(
            stringResource(R.string.profile_academy_error),
            color = MaterialTheme.colorScheme.error
        )
        SaveError(state.error)
        ProfileSubmit(
            stringResource(R.string.profile_confirm),
            isValidAcademy(name) && !state.saving
        ) { model.save(name) }
        if (academyName.isNotBlank()) TextButton(
            onClick = { model.save("", delete = true) },
            enabled = !state.saving
        ) { Text(stringResource(R.string.profile_delete), color = MaterialTheme.colorScheme.error) }
        if (state.saving) CircularProgressIndicator()
    }
}
