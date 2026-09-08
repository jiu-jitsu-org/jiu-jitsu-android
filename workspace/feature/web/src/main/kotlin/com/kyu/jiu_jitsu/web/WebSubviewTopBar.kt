package com.kyu.jiu_jitsu.web

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource

/**
 * 서브 화면의 네이티브 액션 계약. null인 액션은 버튼 자체를 만들지 않는다.
 *
 * 알림 목적지와 더보기 메뉴는 앱별 정책이므로 공통 WebView가 임의 URL이나 JS 메시지를
 * 실행하지 않는다. WebContentRoute의 subviewActions에서 현재 URL에 맞는 callback을
 * 전달하면 해당 버튼이 표시된다. 기본값은 모두 숨김이며, 동작 없는 버튼을 노출하지 않는다.
 * callback은 Compose 수명 안에서만 사용하고 WebView/Activity를 싱글톤에 보관하지 않는다.
 */
data class WebSubviewActions(
    val onNotifications: (() -> Unit)? = null,
    val onMore: (() -> Unit)? = null,
)

/**
 * WebView와 독립된 네이티브 상단 바. 상태바 inset은 app에서 전달한 padding이 이미 처리한다.
 * TopAppBar의 기본 statusBars inset을 다시 적용하면 상단 여백이 두 번 생기므로 0으로 둔다.
 * 숨겨진 부모도 같은 높이를 유지하되 enabled=false로 입력을 막아 복귀 시 레이아웃을 보존한다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WebSubviewTopBar(
    actions: WebSubviewActions,
    enabled: Boolean,
    onBack: () -> Unit,
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBack, enabled = enabled) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.web_back))
            }
        },
        actions = {
            actions.onNotifications?.let { onClick ->
                IconButton(onClick = onClick, enabled = enabled) {
                    Icon(Icons.Outlined.Notifications, stringResource(R.string.web_notifications))
                }
            }
            actions.onMore?.let { onClick ->
                IconButton(onClick = onClick, enabled = enabled) {
                    Icon(Icons.Filled.MoreVert, stringResource(R.string.web_more))
                }
            }
        },
        windowInsets = WindowInsets(0, 0, 0, 0),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black,
        ),
    )
}
