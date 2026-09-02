package com.kyu.jiu_jitsu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.kyu.jiu_jitsu.ui.components.button.PrimaryCTAButton
import com.kyu.jiu_jitsu.ui.components.textfield.TransparentOutlinedTextField
import com.kyu.jiu_jitsu.ui.theme.CoolGray25
import com.kyu.jiu_jitsu.ui.theme.JiuJitsuPjtTheme
import com.kyu.jiu_jitsu.ui.theme.White
import com.kyu.jiu_jitsu.ui.tooling.AdaptiveFontScalePreviews
import com.kyu.jiu_jitsu.ui.tooling.AdaptiveScreenPreviews
import com.kyu.jiu_jitsu.ui.tooling.ImeScreenPreview

@PreviewTest
@AdaptiveScreenPreviews
@Composable
fun adaptiveViewportContractScreenshot() {
    AdaptiveScreenshotTheme {
        AdaptiveFormFixture()
    }
}

@PreviewTest
@AdaptiveFontScalePreviews
@Composable
fun adaptiveFontScaleContractScreenshot() {
    AdaptiveScreenshotTheme {
        AdaptiveFormFixture()
    }
}

@PreviewTest
@ImeScreenPreview
@Composable
fun imeVisibleContractScreenshot() {
    AdaptiveScreenshotTheme {
        AdaptiveFormFixture(
            imeBottomInset = 280.dp,
            showImeCover = true,
        )
    }
}

@Composable
private fun AdaptiveScreenshotTheme(content: @Composable () -> Unit) {
    JiuJitsuPjtTheme(
        darkTheme = false,
        dynamicColor = false,
        content = content,
    )
}

/**
 * Deterministic test-only form that exercises shared text and action components under the canonical
 * viewport, font-scale, and IME matrices. Feature suites must render their own stateless Screen
 * composables; this fixture does not replace feature coverage.
 */
@Composable
private fun AdaptiveFormFixture(
    imeBottomInset: Dp = 0.dp,
    showImeCover: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CoolGray25),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = imeBottomInset),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(Color(0xFF244B78)),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 480.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Adaptive form contract",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Long localized guidance must wrap without hiding the input or action.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    TransparentOutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = "jiujitsu_player",
                        placeholder = "Nickname",
                        onValueChange = {},
                    )
                }
            }

            PrimaryCTAButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .heightIn(min = 51.dp)
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                text = "Continue with the entered nickname",
                onClick = {},
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showImeCover) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(280.dp),
                color = Color(0xFFD4D7DC),
            ) {
                Box(contentAlignment = Alignment.TopCenter) {
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        text = "IME test cover",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF42464D),
                    )
                }
            }
        }
    }
}
