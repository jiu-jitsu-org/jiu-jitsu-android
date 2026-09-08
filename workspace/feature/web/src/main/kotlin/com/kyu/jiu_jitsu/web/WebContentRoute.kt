package com.kyu.jiu_jitsu.web

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kyu.jiu_jitsu.webview.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Reusable container for the trusted frontend. App supplies its native login/signup composition. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WebContentRoute(
    padding: PaddingValues,
    onFullscreenChanged: (Boolean) -> Unit,
    loginContent: @Composable (onFinished: (Boolean) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    initialPath: String = "/",
    model: WebViewModel = hiltViewModel(),
    // URL별로 네이티브 버튼의 표시와 동작을 지정한다. 웹 payload 계약을 확장하지 않으며
    // callback이 없는 버튼은 숨긴다. 호출자는 Compose 상태를 읽어 동적으로 바꿀 수 있다.
    subviewActions: (url: String) -> WebSubviewActions = { WebSubviewActions() },
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val policy = remember(model.origin) { runCatching { WebUrlPolicy(model.origin, model.debuggingEnabled) }.getOrNull() }
    if (policy == null) {
        WebFailure(stringResource(R.string.web_configuration_missing), {}, modifier.padding(padding))
        return
    }
    var controller by remember { mutableStateOf<WebHostController?>(null) }
    var launchedFile by remember { mutableStateOf<FileRequest?>(null) }
    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val request = launchedFile
        launchedFile = null
        if (request != null) scope.launch {
            val uris = withContext(Dispatchers.IO) {
                if (result.resultCode != android.app.Activity.RESULT_OK) null else runCatching {
                    val data = result.data
                    val candidates = data?.clipData?.let { clip -> (0 until clip.itemCount).map { clip.getItemAt(it).uri } }
                        ?: listOfNotNull(data?.data)
                    candidates.distinct().take(if (request.multiple) 3 else 1).filter { uri ->
                        uri.scheme == "content" &&
                            context.packageManager.resolveContentProvider(uri.authority.orEmpty(), 0)?.packageName != context.packageName &&
                            uri.authority != "${context.packageName}.fileprovider" &&
                            context.contentResolver.getType(uri)?.startsWith("image/") == true &&
                            context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { true } == true
                    }.takeIf { it.isNotEmpty() }?.toTypedArray()
                }.getOrNull()
            }
            controller?.finishFile(uris, request)
        }
    }
    val initialUrl = remember(policy, initialPath) {
        (policy.origin + initialPath).also { require(initialPath.startsWith("/") && policy.isTrustedDocument(it)) }
    }
    val host = remember(policy, model, initialUrl) {
        WebHostController(context, policy, initialUrl, model, scope,
            external = { url ->
                try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
                catch (_: ActivityNotFoundException) { Toast.makeText(context, R.string.web_external_unavailable, Toast.LENGTH_SHORT).show() }
            },
            share = { url, title ->
                try {
                    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"; putExtra(Intent.EXTRA_TEXT, url); putExtra(Intent.EXTRA_SUBJECT, title)
                    }, context.getString(R.string.web_share)))
                } catch (_: ActivityNotFoundException) { Toast.makeText(context, R.string.web_external_unavailable, Toast.LENGTH_SHORT).show() }
            },
            chooseFile = { request ->
                if (launchedFile != null) controller?.finishFile(null, request)
                else {
                    launchedFile = request
                    try { fileLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE); type = "image/*"
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, request.multiple)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }) } catch (_: ActivityNotFoundException) { launchedFile = null; controller?.finishFile(null, request) }
                }
            },
        )
    }
    SideEffect { controller = host }
    val fullscreen = host.entries.size > 1 || host.loginVisible
    val updateFullscreen by rememberUpdatedState(onFullscreenChanged)
    LaunchedEffect(fullscreen) { updateFullscreen(fullscreen) }
    DisposableEffect(host) { onDispose { host.destroy(); updateFullscreen(false) } }
    LaunchedEffect(host) {
        var first = true
        model.revisions.collect {
            if (first) { first = false; host.prepare() } else host.sessionChanged(model.token())
        }
    }
    val keyboard = LocalSoftwareKeyboardController.current
    val imeVisible = WindowInsets.isImeVisible
    // 상단 버튼과 시스템 Back을 같은 경로로 연결한다. 표면/로그인을 먼저 닫고,
    // 일반 웹 입력 중에는 IME만 숨긴 뒤 다음 Back에서 guard/히스토리/pop을 판단한다.
    val onBack: () -> Unit = {
        if (host.surface != null || host.loginVisible) host.back()
        else if (imeVisible) keyboard?.hide()
        else host.back()
    }
    BackHandler(enabled = host.canHandleBack() || imeVisible, onBack = onBack)

    Box(modifier) {
        Surface(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding()) {
            Box(Modifier.fillMaxSize()) {
                host.entries.forEachIndexed { index, entry ->
                    key(entry.page.document.id) {
                        val visible = index == host.entries.lastIndex && !host.loginVisible
                        // 모든 entry를 composition에 유지해야 AndroidView가 재생성되지 않는다.
                        // 부모는 그리기/접근성에서 제외하고 실제 WebView는 setVisible의 GONE과
                        // onPause로 입력을 막는다. 상단 바 높이는 유지하여 부모의 viewport가
                        // 자식 push/pop 때 상단 바 유무 때문에 달라지지 않도록 한다.
                        val visibilityModifier = if (visible) Modifier else Modifier
                            .graphicsLayer { alpha = 0f }
                            .clearAndSetSemantics { }
                        Column(Modifier.fillMaxSize().then(visibilityModifier)) {
                            if (index > 0) {
                                WebSubviewTopBar(
                                    actions = subviewActions(entry.page.currentUrl),
                                    enabled = visible,
                                    onBack = onBack,
                                )
                            }
                            // 오류/로딩 overlay는 본문에만 배치한다. READY timeout이나 네트워크
                            // 실패 중에도 상단 뒤로가기로 자식 화면을 닫을 수 있어야 한다.
                            Box(Modifier.weight(1f).fillMaxWidth()) {
                                WebPageView(entry.page, visible, Modifier.fillMaxSize())
                                if (visible && entry.page.loading) WebLoading()
                                if (visible && entry.page.failed) {
                                    WebFailure(stringResource(R.string.web_load_failed), { host.retry(entry.page) })
                                }
                            }
                        }
                    }
                }
                if (host.preparing) WebLoading()
                if (host.preparationFailed) WebFailure(stringResource(R.string.web_load_failed), host::prepare)
            }
        }
        if (host.loginVisible) Surface(Modifier.fillMaxSize()) { loginContent(host::completeLogin) }
    }
    WebNativeSurfaces(host)
}

@Composable
private fun WebLoading() {
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            CircularProgressIndicator()
            Text(stringResource(R.string.web_loading), Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun WebFailure(message: String, retry: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(message)
            Button(onClick = retry, modifier = Modifier.padding(top = 16.dp)) { Text(stringResource(R.string.web_retry)) }
        }
    }
}
