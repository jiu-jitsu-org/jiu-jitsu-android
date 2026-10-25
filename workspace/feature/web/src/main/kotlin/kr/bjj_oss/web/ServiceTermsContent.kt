package kr.bjj_oss.web

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kr.bjj_oss.ui.theme.ColorComponents
import kr.bjj_oss.model.AgreementPolicyType
import kr.bjj_oss.webview.WebPageView
import kr.bjj_oss.webview.WebUrlPolicy
import kr.bjj_oss.webview.WebViewPage

/** 시스템 inset은 app의 전체 화면 컨테이너가 소유하고, 이 화면은 약관 본문/플로팅 닫기만 소유한다. */
@Composable
fun ServiceTermsContent(
    type: AgreementPolicyType,
    origin: String,
    privacyPolicyUrl: String,
    marketingPolicyUrl: String,
    debuggingEnabled: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    // URL 분기는 화면에서 타입으로 처리한다. 문자열 코드나 feature:login의 UI 모델에 의존하지 않는다.
    val initialUrl = remember(type, origin, privacyPolicyUrl, marketingPolicyUrl, debuggingEnabled) {
        when (type) {
            AgreementPolicyType.Service -> origin + privacyPolicyUrl.trim().takeIf { it.isNotEmpty() }
            AgreementPolicyType.Privacy -> origin + privacyPolicyUrl.trim().takeIf { it.isNotEmpty() }
            AgreementPolicyType.Marketing -> origin + marketingPolicyUrl.trim().takeIf { it.isNotEmpty() }
        }
    }
    // 정책 URL은 커뮤니티와 다른 origin일 수 있다. 설정된 문서 경로만 허용하는 전용 정책을 사용한다.
    val policy = remember(initialUrl, debuggingEnabled) {
        initialUrl?.let { runCatching { WebUrlPolicy.forDocument(it, debuggingEnabled) }.getOrNull() }
    }
    var retry by remember { mutableIntStateOf(0) }
    var buttonHeight by remember { mutableIntStateOf(0) }
    val buttonGap = 16.dp
    val contentInset = buttonHeight + with(density) { (buttonGap * 2).roundToPx() }
    // 가입 임시 토큰을 웹에 전달하거나 기존 커뮤니티의 세션 준비/스택을 변경하지 않는다.
    val page = remember(context, policy, initialUrl, retry) {
        policy?.let {
            WebViewPage(
                context, it, requireNotNull(initialUrl), debuggingEnabled,
                onMessage = { _, _, _ -> }, onDocumentEnded = {},
                onExternalUrl = { url ->
                    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
                    catch (_: ActivityNotFoundException) {
                        Toast.makeText(context, R.string.web_external_unavailable, Toast.LENGTH_SHORT).show()
                    }
                },
                onChooseFile = { _, _, callback, _ -> callback.onReceiveValue(null) },
                bridgeEnabled = false,
            )
        }
    }
    DisposableEffect(page) { onDispose { page?.destroy() } }
    SideEffect { page?.setBottomContentInset(contentInset) }

    Surface(modifier.fillMaxSize(), color = Color.White) {
        Box(Modifier.fillMaxSize()) {
            if (page != null) {
                WebPageView(page, visible = true, modifier = Modifier.fillMaxSize())
            }
            if (page?.loading == true) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            if (page == null || page.failed) {
                Column(
                    Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(if (page == null) R.string.web_configuration_missing else R.string.web_load_failed))
                    if (page != null) {
                        Button(onClick = { retry++ }) { Text(stringResource(R.string.web_retry)) }
                    }
                }
            }
            // 실제 측정 높이를 사용하므로 큰 글꼴에서도 마지막 문단을 버튼 위로 올릴 수 있다.
            Button(
                onClick = onClose,
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = buttonGap)
                    .heightIn(min = 56.dp)
                    .onSizeChanged { buttonHeight = it.height },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorComponents.Button.Filled.DefaultBg,
                    contentColor = ColorComponents.Button.Filled.DefaultText,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            ) {
                Text(stringResource(R.string.web_close))
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }
    }
}
