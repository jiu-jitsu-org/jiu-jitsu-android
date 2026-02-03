package com.kyu.jiu_jitsu.profile.components.style

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.TrueWhite

/**
 * 스타일 카드 앞면 레이아웃 (포지션, 기술, 서브미션 카드 앞)
 * @param modifier
 * @param cardShape
 * @param backgroundRes - 카드 배경
 * @param iconRes - 카드 아이콘
 */
@Composable
fun CardFrontLayout(
    modifier: Modifier,
    cardShape: Shape,
    backgroundRes: Int,
    iconRes: Int,
    title: String,
    info: String,
) {
    Card(
        modifier = modifier,
        shape = cardShape
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            // Front Card Background
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = TrueWhite,shape = RoundedCornerShape(10.dp)),
                painter = painterResource(backgroundRes),
                contentScale = ContentScale.Crop,
                contentDescription = "Card Background"
            )
            // Front Card Contents
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 25.dp, start = 25.dp, end = 25.dp, top = 55.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Front Card Icon
                Image(
                    modifier = Modifier.size(80.dp),
                    painter = painterResource(iconRes),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Card Icon"
                )
                Spacer(modifier = Modifier.weight(1f))
                // Front Card Contents
                Column {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = title,
                        style = MaterialTheme.typography.displayLarge,
                        color = ColorComponents.TextFieldDisplay.Default.Placeholder,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier= Modifier.height(15.dp))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = info,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ColorComponents.TextFieldDisplay.Default.Placeholder,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}