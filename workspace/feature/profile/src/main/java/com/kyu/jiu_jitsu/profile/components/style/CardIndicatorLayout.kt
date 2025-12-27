package com.kyu.jiu_jitsu.profile.components.style

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.profile.R
import com.kyu.jiu_jitsu.profile.model.StyleCardIndicator
import com.kyu.jiu_jitsu.ui.theme.ColorComponents

@Composable
fun CardIndicatorLayout(
    modifier: Modifier,
    items: List<StyleCardIndicator>,
    selectedIndex: Int = 0,
    onTabSelected: (index:Int) -> Unit,
) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
        ) {
            itemsIndexed(items) { index, item ->
                val indicator by animateDpAsState(
                    targetValue = if (selectedIndex == index) 65.dp else 45.dp,
                    animationSpec = tween(durationMillis = 250),
                    label = "indicator"
                )
                Box(
                    modifier = Modifier
                        .height(indicator)
                        .width(indicator)
                        .background(color = item.color, shape = RoundedCornerShape(15.dp))
                        .clickable(
                            role = Role.Button,
                            onClick = { onTabSelected(index) }
                        ),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = ColorComponents.Header.Header.Text,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}