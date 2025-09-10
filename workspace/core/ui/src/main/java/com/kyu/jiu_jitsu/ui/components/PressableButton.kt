package com.kyu.jiu_jitsu.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.ui.theme.Black
import com.kyu.jiu_jitsu.ui.theme.Blue1000
import com.kyu.jiu_jitsu.ui.theme.Blue50
import com.kyu.jiu_jitsu.ui.theme.CoolGray25
import com.kyu.jiu_jitsu.ui.theme.CoolGray75
import com.kyu.jiu_jitsu.ui.theme.Typography
import com.kyu.jiu_jitsu.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PressableButton(
    text: String,
    textStyle: TextStyle = Typography.bodyMedium,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    enabledBgColor: Color = Blue50,
    pressedBgColor: Color = Blue1000,
    disabledBgColor: Color = CoolGray75,
    enableTextColor: Color = White,
    pressedTextColor: Color = Black,
    disabledTextColor: Color = CoolGray25,
    roundedCorner: Dp = 12.dp,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 10.dp,
    throttleTime: Long = 700L,
) {
    val interactionSource = remember { MutableInteractionSource() } // ← 반드시 remember
    val isPressed by interactionSource.collectIsPressedAsState()     // 손가락이 닿아있는 동안 true

    var throttleFlag by remember { mutableStateOf(false) }   // Button Click Throttle Flag
    val scope = rememberCoroutineScope()

    val bgColor by animateColorAsState(
        if (!enabled) disabledBgColor
        else if (isPressed) pressedBgColor
        else enabledBgColor,
        label = "bg"
    )

    val textColor by animateColorAsState(
        if (!enabled) disabledTextColor
        else if (isPressed) pressedTextColor
        else enableTextColor,
        label = "textColor"
    )

    val scale by animateFloatAsState(if (enabled && isPressed) 0.98f else 1f, label = "scale")

    Box(
        modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(roundedCorner))
            .background(bgColor)
            .clickable(                     // onClick은 보통 "손을 뗄 때" 호출
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                onClick = {
                    // 연속 터치를 막기 위함 ThrottleFlag, ThrottleTime
                    if (!throttleFlag) {
                        onClick()
                        throttleFlag = true
                        scope.launch {
                            delay(throttleTime)
                            throttleFlag = false
                        }
                    }
                }
            )
            .padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding,
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = textColor,
            style = textStyle,
        )
    }
}