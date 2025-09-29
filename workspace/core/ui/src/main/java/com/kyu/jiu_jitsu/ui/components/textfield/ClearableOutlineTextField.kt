package com.kyu.jiu_jitsu.ui.components.textfield

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.ui.theme.CoolGray25
import com.kyu.jiu_jitsu.ui.theme.CoolGray50
import com.kyu.jiu_jitsu.ui.theme.Red500

enum class ValidationState { Default, Valid, Invalid }

@Composable
fun ClearableOutlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    validator: (String) -> ValidationState = { ValidationState.Default },      // 유효성 측정: 입력값 -> Default / Invalid / Neutral
    shape: Shape = RoundedCornerShape(12.dp),
    defaultBorder: Color = CoolGray25,
    successBorder: Color = CoolGray50,
    errorBorder: Color = Red500,
    cursorColor: Color = CoolGray50,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    ),
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    var focused by remember { mutableStateOf(false) }
    val state = remember(value) { validator(value) }

    // 유효성에 따른 테두리 색상 애니메이션 (spring으로 부드럽게)
    val target = when (state) {
        ValidationState.Valid -> successBorder
        ValidationState.Invalid -> errorBorder
        ValidationState.Default -> if (focused) successBorder.copy(alpha = 0.6f) else defaultBorder
    }
    val animatedBorder by animateColorAsState(
        targetValue = target,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "borderAnim"
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .onFocusChanged {
                val nowFocused = it.isFocused
                // 포커스가 사라지면 키보드 숨김
                if (focused && !nowFocused) keyboard?.hide()
                focused = nowFocused
            },
        isError = state == ValidationState.Invalid,
        singleLine = singleLine,
        label = { if (label != null) Text(label) },
        placeholder = { if (placeholder != null) Text(placeholder) },
        visualTransformation = visualTransformation,

        // X 버튼 (입력 시작하면 노출)
        trailingIcon = {
            AnimatedVisibility(
                visible = value.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                IconButton(
                    onClick = { onValueChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear text"
                    )
                }
            }
        },

        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = cursorColor,
            focusedBorderColor = animatedBorder,
            unfocusedBorderColor = animatedBorder,
            errorBorderColor = errorBorder
        ),

        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),

        shape = shape
    )
}