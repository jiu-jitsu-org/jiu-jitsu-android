package com.kyu.jiu_jitsu.setting

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kyu.jiu_jitsu.setting.screen.SettingScreen

/**
 * 설정 화면의 진입점. 향후 ViewModel의 상태를 수집하고 화면 이벤트와 이동 콜백을 연결한다.
 * 현재는 네비게이션 연결만 가능한 임시 구조이며, 상태와 비즈니스 로직은 구현하지 않는다.
 */
@Composable
fun SettingRoute(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
) {
    SettingScreen(modifier = modifier, padding = padding)
}
