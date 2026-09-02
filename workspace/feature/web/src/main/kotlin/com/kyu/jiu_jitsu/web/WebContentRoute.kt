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
    BackHandler(enabled = host.canHandleBack() || imeVisible) {
        if (imeVisible && host.surface == null) keyboard?.hide() else host.back()
    }

    Box(modifier) {
        Surface(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding()) {
            Box(Modifier.fillMaxSize()) {
                host.entries.forEachIndexed { index, entry ->
                    key(entry.page.document.id) {
                        val visible = index == host.entries.lastIndex && !host.loginVisible
                        Column(Modifier.fillMaxSize()) {
                            if (visible && index > 0 && (policy.needsNativeClose(entry.page.currentUrl) || entry.page.failed)) {
                                TextButton(onClick = host::back) { Text(stringResource(R.string.web_close)) }
                            }
                            WebPageView(entry.page, visible, Modifier.weight(1f).fillMaxWidth())
                        }
                        if (visible && entry.page.loading) WebLoading()
                        if (visible && entry.page.failed) {
                            WebFailure(stringResource(R.string.web_load_failed), { host.retry(entry.page) })
                            if (index > 0) TextButton(onClick = host::back) { Text(stringResource(R.string.web_close)) }
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
