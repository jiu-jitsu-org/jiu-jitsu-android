package kr.bjj_oss.ui.tooling

import androidx.compose.ui.tooling.preview.Preview

/**
 * Canonical viewport matrix for screen-level adaptive UI screenshot tests.
 *
 * These viewports are regression-test fixtures, not product breakpoints. Production layout decisions
 * must continue to use the constraints offered to the composable.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@Preview(
    name = "compact_320x568",
    group = "adaptive_size",
    widthDp = 320,
    heightDp = 568,
    showBackground = true,
)
@Preview(
    name = "phone_360x800",
    group = "adaptive_size",
    widthDp = 360,
    heightDp = 800,
    showBackground = true,
)
@Preview(
    name = "expanded_600x960",
    group = "adaptive_size",
    widthDp = 600,
    heightDp = 960,
    showBackground = true,
)
@Preview(
    name = "landscape_800x360",
    group = "adaptive_size",
    widthDp = 800,
    heightDp = 360,
    showBackground = true,
)
annotation class AdaptiveScreenPreviews

/** Canonical font-scale matrix for text-bearing screens and components. */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@Preview(
    name = "font_1_0",
    group = "adaptive_font",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    showBackground = true,
)
@Preview(
    name = "font_1_3",
    group = "adaptive_font",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.3f,
    showBackground = true,
)
@Preview(
    name = "font_2_0",
    group = "adaptive_font",
    widthDp = 360,
    heightDp = 800,
    fontScale = 2.0f,
    showBackground = true,
)
annotation class AdaptiveFontScalePreviews

/**
 * Viewport used for an IME-visible screenshot case.
 *
 * The annotated composable must inject a deterministic bottom IME inset and render a test-only IME
 * cover because Layoutlib previews do not open the platform keyboard.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@Preview(
    name = "ime_360x800",
    group = "adaptive_ime",
    widthDp = 360,
    heightDp = 800,
    showBackground = true,
)
annotation class ImeScreenPreview
