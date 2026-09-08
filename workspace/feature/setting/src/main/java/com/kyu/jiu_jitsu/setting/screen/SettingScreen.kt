package com.kyu.jiu_jitsu.setting.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 전달받은 상태와 이벤트 콜백으로 설정 UI를 표시할 화면. 현재는 빈 콘텐츠만 배치한다.
 * app의 Scaffold가 전달한 여백을 여기서 한 번 적용하며, 바텀 네비게이션은 app이 소유한다.
 */
@Composable
internal fun SettingScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
) {
    Box(modifier = modifier.padding(padding)) {
        // 설정 메뉴와 항목별 UI는 화면 요구사항이 정해진 뒤 구현한다.
    }
}
