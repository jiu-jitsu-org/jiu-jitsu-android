package kr.bjj_oss.setting.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kr.bjj_oss.setting.R
import kr.bjj_oss.setting.components.SettingItem
import kr.bjj_oss.setting.model.SettingUiState
import kr.bjj_oss.ui.theme.ColorComponents
import kr.bjj_oss.ui.theme.ColorSemantic

@Composable
internal fun SettingScreen(
    state: SettingUiState,
    versionName: String,
    onBack: () -> Unit,
    onNotifications: () -> Unit,
    onTerms: () -> Unit,
    onPrivacy: () -> Unit,
    onLogin: () -> Unit,
    onLogout: () -> Unit,
    onWithdrawal: () -> Unit,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
) {
    Column(modifier.fillMaxSize().background(ColorSemantic.Surface.BackgroundDefault).padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, stringResource(R.string.setting_back))
            }
            Text(stringResource(R.string.setting_title), modifier = Modifier.align(Alignment.Center).padding(vertical = 16.dp),
                style = MaterialTheme.typography.titleMedium)
        }
        Column(
            Modifier.widthIn(max = 600.dp).fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isLoggedIn == true) {
                SettingGroup {
                    SettingItem(stringResource(R.string.setting_notifications), Icons.Outlined.NotificationsNone, onClick = onNotifications)
                }
            }
            SettingGroup {
                SettingItem(stringResource(R.string.setting_terms), Icons.AutoMirrored.Outlined.ListAlt, onClick = onTerms)
                SettingItem(stringResource(R.string.setting_privacy), Icons.AutoMirrored.Outlined.ListAlt, onClick = onPrivacy)
            }
            SettingGroup {
                SettingItem(stringResource(R.string.setting_version), Icons.Outlined.Build, value = versionName)
            }
            if (state.isLoggedIn != null) {
                SettingGroup {
                    SettingItem(
                        stringResource(if (state.isLoggedIn) R.string.setting_logout else R.string.setting_login),
                        Icons.Outlined.LockOpen, enabled = !state.isLoggingOut,
                        onClick = if (state.isLoggedIn) onLogout else onLogin,
                    )
                    if (state.isLoggedIn) {
                        SettingItem(stringResource(R.string.setting_withdrawal), Icons.AutoMirrored.Outlined.ExitToApp,
                            enabled = !state.isLoggingOut, onClick = onWithdrawal)
                    }
                }
            }
            if (state.isLoggingOut) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            if (state.logoutFailed) {
                Text(stringResource(R.string.setting_logout_error), color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun SettingGroup(content: @Composable ColumnScope.() -> Unit) {
    Surface(shape = RoundedCornerShape(18.dp), color = ColorComponents.List.Setting.Background) {
        Column(Modifier.fillMaxWidth().padding(vertical = 4.dp), content = content)
    }
}
