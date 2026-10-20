package kr.bjj_oss.web

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

/** 앱 내 목적지 연결은 호출자가 담당한다. 기본 동작은 후속 구현 전까지 비워 둔다. */
data class WebRootActions(
    val onNotifications: () -> Unit = {},
    val onSearch: () -> Unit = {},
    val onProfile: () -> Unit = {},
)

internal enum class WebRootTab { Feed, Category }

/** 고정 헤더. 시스템 inset은 Route가 적용하며 본문 스크롤과 연결하지 않는다. */
@Composable
internal fun WebRootTopBar(
    selectedTab: WebRootTab,
    categoryEnabled: Boolean,
    enabled: Boolean,
    onTabSelected: (WebRootTab) -> Unit,
    actions: WebRootActions,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier, color = MaterialTheme.colorScheme.surface) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = 55.dp).padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f).selectableGroup(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    WebRootTab.entries.forEach { tab ->
                        val selected = selectedTab == tab
                        val tabEnabled = enabled && (tab == WebRootTab.Feed || categoryEnabled)
                        val color = when {
                            !tabEnabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            selected -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        Column(
                            modifier = Modifier
                                .selectable(selected, enabled = tabEnabled, role = Role.Tab) { onTabSelected(tab) }
                                .heightIn(min = 48.dp)
                                .padding(horizontal = 4.dp),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = stringResource(if (tab == WebRootTab.Feed) R.string.web_feed else R.string.web_category),
                                color = color,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                            Box(Modifier.width(24.dp).height(2.dp).background(
                                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            ))
                        }
                    }
                }
                IconButton(onClick = actions.onNotifications, enabled = enabled) {
                    Icon(Icons.Outlined.Notifications, stringResource(R.string.web_notifications))
                }
                IconButton(onClick = actions.onSearch, enabled = enabled) {
                    Icon(Icons.Outlined.Search, stringResource(R.string.web_search))
                }
                IconButton(onClick = actions.onProfile, enabled = enabled) {
                    Icon(Icons.Outlined.AccountCircle, stringResource(R.string.web_profile))
                }
            }
            HorizontalDivider()
        }
    }
}
