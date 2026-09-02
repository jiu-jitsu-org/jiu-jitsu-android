package com.kyu.jiu_jitsu.web

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.kyu.jiu_jitsu.webview.ConfirmResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WebNativeSurfaces(controller: WebHostController) {
    when (val surface = controller.surface) {
        is WebSurface.Confirm -> {
            val request = surface.request
            AlertDialog(
                onDismissRequest = controller::dismissSurface,
                properties = DialogProperties(dismissOnClickOutside = request.dismissOnOutsideTap),
                title = { Text(request.title) },
                text = request.message?.let { { Text(it, Modifier.verticalScroll(rememberScrollState())) } },
                confirmButton = { TextButton(onClick = { controller.confirm(ConfirmResult.CONFIRM) }) {
                    Text(request.confirmText, color = if (request.destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                } },
                dismissButton = { TextButton(onClick = { controller.confirm(ConfirmResult.CANCEL) }) {
                    Text(request.cancelText ?: stringResource(R.string.web_cancel))
                } },
            )
        }
        is WebSurface.Select -> key(surface) {
            var value by remember { mutableStateOf<String?>(null) }
            var custom by remember { mutableStateOf("") }
            ModalBottomSheet(
                onDismissRequest = controller::dismissSurface,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            ) {
                Column(Modifier.fillMaxWidth().heightIn(max = 640.dp).padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
                    Text(surface.request.title, style = MaterialTheme.typography.titleLarge)
                    surface.request.message?.let { Text(it, Modifier.padding(vertical = 12.dp)) }
                    surface.request.options.forEach { option ->
                        Row(Modifier.fillMaxWidth().heightIn(min = 48.dp)
                            .selectable(value == option.value, role = Role.RadioButton, onClick = { value = option.value })
                            .padding(vertical = 4.dp)) {
                            RadioButton(selected = value == option.value, onClick = null)
                            Text(option.label, Modifier.padding(start = 12.dp).weight(1f))
                        }
                    }
                    if (surface.request.options.any { it.value == value && it.allowsCustomText }) {
                        OutlinedTextField(value = custom, onValueChange = { custom = it }, modifier = Modifier.fillMaxWidth(),
                            placeholder = surface.request.customTextPlaceholder?.let { { Text(it) } })
                    }
                    Button(onClick = { controller.select(value, custom) }, enabled = value != null,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) { Text(surface.request.submitText) }
                }
            }
        }
        is WebSurface.Login -> AlertDialog(
            onDismissRequest = controller::dismissSurface,
            title = { Text(stringResource(R.string.web_login_title)) },
            text = { Text(stringResource(R.string.web_login_message)) },
            confirmButton = { TextButton(onClick = controller::startLogin) { Text(stringResource(R.string.web_login)) } },
            dismissButton = { TextButton(onClick = controller::dismissSurface) { Text(stringResource(R.string.web_cancel)) } },
        )
        null -> Unit
    }
}
