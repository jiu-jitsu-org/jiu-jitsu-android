package com.kyu.jiu_jitsu.ui.components.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.Typography

@Composable
fun NeutralButton(
    text: String,
    textStyle: TextStyle = Typography.bodyMedium,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    enabledBgColor: Color = ColorComponents.Button.Neutral.DefaultBg,
    pressedBgColor: Color = ColorComponents.Button.Neutral.PressedBg,
    disabledBgColor: Color = ColorComponents.Button.Neutral.DisabledBg,
    enableTextColor: Color = ColorComponents.Button.Neutral.DefaultText,
    pressedTextColor: Color = ColorComponents.Button.Neutral.PressedText,
    disabledTextColor: Color = ColorComponents.Button.Neutral.DisabledText,
    roundedCorner: Dp = 15.dp,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 10.dp,
) {
    PressableButton(
        text = text,
        textStyle = textStyle,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        enabledBgColor = enabledBgColor,
        pressedBgColor = pressedBgColor,
        disabledBgColor = disabledBgColor,
        enableTextColor = enableTextColor,
        pressedTextColor = pressedTextColor,
        disabledTextColor = disabledTextColor,
        roundedCorner = roundedCorner,
        horizontalPadding = horizontalPadding,
        verticalPadding = verticalPadding,
    )
}