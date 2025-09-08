package com.kyu.jiu_jitsu.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kyu.jiu_jitsu.ui.R

internal val PretendardFont = FontFamily(
    Font(R.font.pretendard_bold, FontWeight.Bold),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_medium, FontWeight.Normal),
    Font(R.font.pretendard_light, FontWeight.Light),
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
internal val JiuJitsuTypography = Typography(
    // Title1 - 페이지 화면 최상위 타이틀
    titleLarge = TextStyle(
        fontFamily = PretendardFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
    ),
    // Title1 (Multi-line) - 두 줄 이상 타이틀
    titleLargeEmphasized = TextStyle(
        fontFamily = PretendardFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 32.sp,
    ),
    // Title2 -
    titleMedium = TextStyle(
        fontFamily = PretendardFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
    ),
    // Title3 -
    titleSmall = TextStyle(
        fontFamily = PretendardFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
    ),
    // Body M - 일반 본문 텍스트 (주요 설명문, 콘텐츠 영역)
    bodyMedium = TextStyle(
        fontFamily = PretendardFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
    ),
    // Body M (Multi-line) - 두 줄 이상 본문 텍스트
    bodyMediumEmphasized = TextStyle(
        fontFamily = PretendardFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    // Body S - 부가 설명, 세부 안내
    bodySmall = TextStyle(
        fontFamily = PretendardFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
    // Body S (Multi-line) - 두 줄 이상 본문 텍스트
    bodySmallEmphasized = TextStyle(
        fontFamily = PretendardFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 21.sp,
    ),
    // Label M - 이미지 캡션, 작은 라벨, 작은 안내 텍스트
    labelMedium = TextStyle(
        fontFamily = PretendardFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
    ),
    // Label M (Multi-line) - 이미지 캡션, 작은 라벨, 작은 안내 텍스트
    labelMediumEmphasized = TextStyle(
        fontFamily = PretendardFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp,
    ),
    // Label S - 뱃지 안 텍스트
    labelSmall = TextStyle(
        fontFamily = PretendardFont,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
    ),
    // Button L
    headlineLarge = TextStyle(
        fontFamily = PretendardFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    // Button M
    headlineMedium = TextStyle(
        fontFamily = PretendardFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    // Button S
    headlineSmall = TextStyle(
        fontFamily = PretendardFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    ),
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)