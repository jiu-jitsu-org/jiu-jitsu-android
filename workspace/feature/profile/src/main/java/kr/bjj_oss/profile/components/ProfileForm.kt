package kr.bjj_oss.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kr.bjj_oss.profile.R
import kr.bjj_oss.ui.theme.White

@Composable
fun ProfileForm(
    title: String,
    padding: PaddingValues,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(modifier.fillMaxSize(), color = White) {
        Box(Modifier.padding(padding).consumeWindowInsets(padding).imePadding().fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(Modifier.widthIn(max = 600.dp).fillMaxSize()) {
                Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(end = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.profile_icon_arrow_left), stringResource(R.string.profile_back))
                    }
                    Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                    actions()
                }
                Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp), content = content)
            }
        }
    }
}

@Composable
fun ProfileSubmit(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth().heightIn(min = 51.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)) { Text(text) }
}

@Composable
fun SaveError(visible: Boolean) {
    if (visible) Text(stringResource(R.string.profile_save_error), color = MaterialTheme.colorScheme.error)
}
