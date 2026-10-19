package kr.bjj_oss.setting

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable
import kr.bjj_oss.setting.screen.NotificationScreen

@Serializable
data object NotificationDestination

@Composable
fun NotificationRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
) {
    // UI-only defaults from the design. These do not represent saved notification consent.
    // TODO: Observe and update preferences through a repository once the API contract is available.
    var security by rememberSaveable { mutableStateOf(true) }
    var service by rememberSaveable { mutableStateOf(true) }
    var community by rememberSaveable { mutableStateOf(true) }
    var marketing by rememberSaveable { mutableStateOf(false) }

    NotificationScreen(
        security = security,
        service = service,
        community = community,
        marketing = marketing,
        onSecurityChange = { security = it },
        onServiceChange = { service = it },
        onCommunityChange = { community = it },
        onMarketingChange = { marketing = it },
        onBack = onBack,
        modifier = modifier,
        padding = padding,
    )
}
