package com.kyu.jiu_jitsu.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kyu.jiu_jitsu.ui.components.textfield.ClearableOutlineTextField
import com.kyu.jiu_jitsu.ui.components.CommonDialog
import com.kyu.jiu_jitsu.ui.components.textfield.ValidationState
import com.kyu.jiu_jitsu.ui.theme.CoolGray75

@Composable
fun GrayScreen() {
    var text by rememberSaveable { mutableStateOf("") }
    var isShowDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CoolGray75,
    ) {
        Column {
            ClearableOutlineTextField(
                value = text,
                onValueChange = { new -> text = new },
                label = "Label",
                placeholder = "Placeholder",
                validator = { value -> if (value.length > 5) ValidationState.Invalid else ValidationState.Default }
            )

            Button(
                onClick = {
                    isShowDialog = !isShowDialog
                }
            ) {
                Text("Show Dialog")
            }
        }

        if (isShowDialog) {
            CommonDialog(
                title = "Title",
                message = "Message",
                primaryButtonText = "Primary",
                onPrimaryClick = {
                    isShowDialog = !isShowDialog
                },
                onDismissRequest = {
                    isShowDialog = !isShowDialog
                },
                secondaryButtonText = "Secondary",
                onSecondaryClick = {
                    isShowDialog = !isShowDialog
                },
                dismissOnClickOutside = false
            )
        }
    }

}