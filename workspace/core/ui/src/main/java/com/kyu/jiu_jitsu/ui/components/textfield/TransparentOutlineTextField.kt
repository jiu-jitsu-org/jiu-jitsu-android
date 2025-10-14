package com.kyu.jiu_jitsu.ui.components.textfield

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import com.kyu.jiu_jitsu.ui.theme.ColorComponents

@Composable
fun TransparentOutlinedTextField(
    value: String,
    placeholder: String = "",
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusedTextColor: Color = ColorComponents.TextFieldDisplay.Focus.Text,
    errorTextColor: Color = ColorComponents.TextFieldDisplay.Error.Text,
    cursorColor: Color = ColorComponents.TextFieldDisplay.Focus.Text,
    isError: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        textStyle = MaterialTheme.typography.displayLarge,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                placeholder,
                style = MaterialTheme.typography.displayLarge,
                color = ColorComponents.TextFieldDisplay.Default.Placeholder
            )
        },
        isError = isError,
        singleLine = true,
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            // 배경
            focusedContainerColor = Transparent,
            unfocusedContainerColor = Transparent,
            disabledContainerColor = Transparent,
            errorContainerColor = Transparent,
            // 외곽선
            focusedBorderColor = Transparent,
            unfocusedBorderColor = Transparent,
            disabledBorderColor = Transparent,
            errorBorderColor = Transparent,
            // 입력 텍스트 색
            focusedTextColor        = focusedTextColor,
            unfocusedTextColor      = ColorComponents.TextFieldDisplay.Default.Title,
            disabledTextColor       = ColorComponents.TextFieldDisplay.Default.Title,
            errorTextColor          = errorTextColor,
            // 커서 색
            cursorColor             = cursorColor,
        )
    )
}
