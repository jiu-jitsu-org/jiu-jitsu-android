package com.kyu.jiu_jitsu.setting

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kyu.jiu_jitsu.setting.screen.SettingScreen
import com.kyu.jiu_jitsu.setting.viewmodel.SettingViewModel

/** Connects session state and app navigation to the stateless settings UI. */
@Composable
fun SettingRoute(
    versionName: String,
    onBack: () -> Unit,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
) {
    val model: SettingViewModel = hiltViewModel()
    val state by model.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val openPage: (String) -> Unit = { url ->
        // TODO: Replace the temporary browser destinations with the finalized detail-page flow.
        // Do not attach session tokens or personal data to these placeholder URLs.
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, R.string.setting_page_unavailable, Toast.LENGTH_SHORT).show()
        }
    }
    SettingScreen(
        state = state, versionName = versionName, onBack = onBack,
        onNotifications = { if (state.isLoggedIn == true) openPage(SettingUrls.NOTIFICATIONS) },
        onTerms = { openPage(SettingUrls.TERMS) },
        onPrivacy = { openPage(SettingUrls.PRIVACY) },
        onLogin = onLogin, onLogout = model::logout,
        onWithdrawal = { if (state.isLoggedIn == true) openPage(SettingUrls.WITHDRAWAL) },
        modifier = modifier, padding = padding,
    )
}
