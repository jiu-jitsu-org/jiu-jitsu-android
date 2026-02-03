package com.kyu.jiu_jitsu.profile.components.style

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.ui.theme.ColorComponents

/**
 * 스타일 카드 뒷면 레이아웃 (포지션, 기술, 서브미션 카드 뒤)
 * @param modifier
 * @param cardShape
 * @param bgColor - 카드 배경
 */
@Composable
fun CardBackLayout(
    modifier: Modifier,
    cardShape: Shape,
    bgColor: Color = Color(0xFF243042),
    type: String,
    title: String,
) {
    Card(
        modifier = modifier,
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = type,
                style = MaterialTheme.typography.bodyMedium,
                color = ColorComponents.TextFieldDisplay.Default.Placeholder,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier= Modifier.height(15.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.displayLarge,
                color = ColorComponents.TextFieldDisplay.Default.Placeholder,
                textAlign = TextAlign.Center,
            )
        }
    }
}