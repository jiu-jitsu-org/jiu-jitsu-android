package kr.bjj_oss.setting.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kr.bjj_oss.setting.R
import kr.bjj_oss.ui.theme.ColorComponents
import kr.bjj_oss.ui.theme.ColorSemantic

@Composable
internal fun NotificationScreen(
    security: Boolean,
    service: Boolean,
    community: Boolean,
    marketing: Boolean,
    onSecurityChange: (Boolean) -> Unit,
    onServiceChange: (Boolean) -> Unit,
    onCommunityChange: (Boolean) -> Unit,
    onMarketingChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
) {
    Column(
        modifier.fillMaxSize().background(ColorSemantic.Surface.BackgroundDefault).padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(
                    Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.setting_back),
                    tint = ColorComponents.Header.Header.IconButton,
                )
            }
            Text(
                stringResource(R.string.setting_notifications),
                modifier = Modifier.align(Alignment.Center).padding(vertical = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                color = ColorComponents.Header.Header.Text,
            )
        }
        Column(
            Modifier.widthIn(max = 600.dp).fillMaxWidth().weight(1f)
                .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            Surface(shape = RoundedCornerShape(16.dp), color = ColorComponents.List.Setting.Background) {
                Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    NotificationItem(stringResource(R.string.notification_security),
                        stringResource(R.string.notification_security_description), security, onSecurityChange)
                    NotificationItem(stringResource(R.string.notification_service),
                        stringResource(R.string.notification_service_description), service, onServiceChange)
                    NotificationItem(stringResource(R.string.notification_community),
                        stringResource(R.string.notification_community_description), community, onCommunityChange)
                    NotificationItem(stringResource(R.string.notification_marketing),
                        stringResource(R.string.notification_marketing_description), marketing, onMarketingChange)
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.fillMaxWidth().toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .heightIn(min = 52.dp).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(Icons.Outlined.NotificationsNone, contentDescription = null,
            modifier = Modifier.size(20.dp), tint = ColorComponents.List.Setting.Icon)
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = ColorComponents.List.Setting.Text)
            Text(description, style = MaterialTheme.typography.bodySmall, color = ColorComponents.List.Setting.SubText)
        }
        // The whole row provides a single accessible switch target; the visual track is decorative.
        val thumbOffset by animateDpAsState(if (checked) 22.dp else 2.dp, label = "notificationThumb")
        Box(
            Modifier.size(width = 58.dp, height = 26.dp).background(
                if (checked) ColorComponents.Switch.On.Bg else ColorComponents.Switch.Off.Bg,
                RoundedCornerShape(50),
            ),
        ) {
            Box(Modifier.offset(x = thumbOffset, y = 2.dp).size(width = 34.dp, height = 22.dp)
                .background(ColorComponents.Switch.On.Thumb, RoundedCornerShape(50)))
        }
    }
}
