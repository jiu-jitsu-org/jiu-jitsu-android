package com.kyu.jiu_jitsu.ui.components.toggle

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CommonToggleButton(
    isChecked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    onColor: Color = Color(0xFF4CAF50),
    offColor: Color = Color(0xFFF44336)
) {
    val transition = updateTransition(targetState = isChecked, label = "switch")
    val thumbOffset by transition.animateDp(label = "thumbOffset") {
        if (it) 24.dp else 0.dp
    }
    val backgroundColor by transition.animateColor(label = "backgroundColor") {
        if (it) onColor else offColor
    }

    Box(
        modifier = modifier
            .width(50.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .clickable { onToggle() }
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .offset(x = thumbOffset)
                .background(Color.White, CircleShape)
        )
    }
}