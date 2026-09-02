package com.kyu.jiu_jitsu.webview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.UUID

/** Activity/composition owned. Never keep this object in a ViewModel or application singleton. */
@SuppressLint("SetJavaScriptEnabled")
class WebViewPage(
    context: Context,
    val policy: WebUrlPolicy,
    initialUrl: String,
    debug: Boolean,
    private val onMessage: (WebViewPage, Long, WebMessage) -> Unit,
    private val onDocumentEnded: (WebViewPage) -> Unit,
    private val onExternalUrl: (String) -> Unit,
    private val onChooseFile: (WebViewPage, Long, ValueCallback<Array<Uri>>, WebChromeClient.FileChooserParams) -> Unit,
) {
    val document = WebDocument(UUID.randomUUID().toString())
    var loading by mutableStateOf(true); private set
    var failed by mutableStateOf(false); private set
    var backGuard by mutableStateOf(false); private set
    var canGoBack by mutableStateOf(false); private set
    var currentUrl by mutableStateOf(initialUrl); private set
    private val handler = Handler(Looper.getMainLooper())
    private var readyBinding = false
    private var disposed = false
    private var rendererGone = false
    private var pendingBack = false
    private var loaded = false
    private var visible = true
    val view = WebView(context)

    init {
        require(policy.isTrustedDocument(initialUrl))
        WebView.setWebContentsDebuggingEnabled(debug)
        view.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            allowContentAccess = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
            useWideViewPort = true
            loadWithOverviewMode = true
            safeBrowsingEnabled = true
        }
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(view, false)
        }
        view.addJavascriptInterface(Transport(), "AppBridge")
        view.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(v: WebView, callback: ValueCallback<Array<Uri>>, params: FileChooserParams): Boolean {
                if (!isCurrent(document.generation) || !visible) { callback.onReceiveValue(null); return true }
                onChooseFile(this@WebViewPage, document.generation, callback, params)
                return true
            }
            override fun onPermissionRequest(request: PermissionRequest) { request.deny() }
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean = true
        }
        view.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(v: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                if (policy.isTrustedDocument(url)) return false
                if (request.isForMainFrame && request.hasGesture() && policy.isExternalWebUrl(url)) onExternalUrl(url)
                return true
            }
            override fun onPageStarted(v: WebView, url: String?, favicon: Bitmap?) {
                endDocument()
                if (!policy.isTrustedDocument(url)) {
                    v.stopLoading()
                    v.removeJavascriptInterface("AppBridge")
                    v.settings.javaScriptEnabled = false
                    fail()
                    return
                }
                currentUrl = requireNotNull(url)
                loading = true
                failed = false
                val generation = document.generation
                handler.postDelayed({ if (document.matches(generation) && !document.ready) fail() }, READY_TIMEOUT_MS)
            }
            override fun doUpdateVisitedHistory(v: WebView, url: String?, isReload: Boolean) {
                canGoBack = v.canGoBack()
                if (policy.isTrustedDocument(url)) currentUrl = requireNotNull(url)
            }
            override fun onReceivedError(v: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (request.isForMainFrame) fail()
            }
            override fun onReceivedHttpError(v: WebView, request: WebResourceRequest, response: WebResourceResponse) {
                if (request.isForMainFrame) fail()
            }
            override fun onReceivedSslError(v: WebView, handler: SslErrorHandler, error: SslError) {
                handler.cancel()
                fail()
            }
            override fun onRenderProcessGone(v: WebView, detail: RenderProcessGoneDetail): Boolean {
                endDocument()
                rendererGone = true
                (v.parent as? ViewGroup)?.removeView(v)
                v.destroy()
                failed = true
                loading = false
                return true
            }
        }
    }

    fun load() {
        if (loaded || disposed) return
        loaded = true
        view.loadUrl(currentUrl)
    }

    fun isCurrent(generation: Long): Boolean = !disposed && !rendererGone &&
        document.matches(generation) && policy.isTrustedDocument(view.url ?: currentUrl)

    fun send(generation: Long, message: NativeMessage) {
        check(Looper.myLooper() == Looper.getMainLooper())
        if (!isCurrent(generation) || failed) return
        if (document.enqueue(generation, message)) flush()
    }

    private fun flush() {
        if (!isCurrent(document.generation)) return
        val generation = document.generation
        document.drain().forEach { message ->
            view.evaluateJavascript(BridgeCodec.script(policy.origin, document.key, message)) { delivered ->
                if (delivered != "true" && isCurrent(generation)) fail()
            }
        }
    }

    /** Returns true while the web owns the close decision. */
    fun requestBack(): Boolean {
        if (failed || !document.backGuard) return false
        if (!pendingBack) {
            pendingBack = true
            send(document.generation, NativeMessage.back)
            // Prevent repeated system back from stacking confirmations before the web responds.
            val generation = document.generation
            handler.postDelayed({ if (document.matches(generation)) pendingBack = false }, 1000)
        }
        return true
    }

    fun setVisible(value: Boolean, resumed: Boolean) {
        if (disposed || rendererGone) return
        visible = value
        view.visibility = if (value) View.VISIBLE else View.GONE
        if (value && resumed) view.onResume() else view.onPause()
    }

    fun destroy() {
        if (disposed) return
        disposed = true
        onDocumentEnded(this)
        document.destroy()
        handler.removeCallbacksAndMessages(null)
        if (!rendererGone) {
            view.stopLoading()
            view.removeJavascriptInterface("AppBridge")
            view.webChromeClient = null
            (view.parent as? ViewGroup)?.removeView(view)
            view.destroy()
        }
    }

    private fun endDocument() {
        onDocumentEnded(this)
        document.start()
        backGuard = false
        canGoBack = false
        readyBinding = false
        pendingBack = false
    }

    private fun fail() {
        if (disposed || failed) return
        endDocument()
        failed = true
        loading = false
    }

    private inner class Transport {
        @JavascriptInterface
        fun postMessage(raw: String) {
            val generation = document.generation
            val message = BridgeCodec.decode(raw) ?: return
            handler.post {
                if (!isCurrent(generation) || failed) return@post
                if (message == WebMessage.Ready) {
                    if (document.ready || readyBinding) return@post
                    readyBinding = true
                    val script = """(() => {
                        if (location.origin !== ${BridgeCodec.literal(policy.origin)}) return false;
                        if (typeof window.WebBridge?.receive !== "function") return false;
                        window.__ossNativeDocument = ${BridgeCodec.literal(document.key)};
                        return true;
                    })();""".trimIndent()
                    view.evaluateJavascript(script) { result ->
                        if (!isCurrent(generation)) return@evaluateJavascript
                        readyBinding = false
                        if (result != "true") { fail(); return@evaluateJavascript }
                        if (document.markReady()) {
                            loading = false
                            onMessage(this@WebViewPage, generation, message)
                            flush()
                        }
                    }
                } else {
                    if (message is WebMessage.BackGuard) {
                        document.backGuard = message.enabled
                        backGuard = message.enabled
                    }
                    onMessage(this@WebViewPage, generation, message)
                }
            }
        }
    }

    companion object { private const val READY_TIMEOUT_MS = 20_000L }
}

@Composable
fun WebPageView(page: WebViewPage, visible: Boolean, modifier: Modifier = Modifier) {
    val owner = LocalLifecycleOwner.current
    DisposableEffect(page, owner, visible) {
        val observer = LifecycleEventObserver { _, _ ->
            page.setVisible(visible, owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    AndroidView(factory = { page.view }, modifier = modifier, update = {
        page.setVisible(visible, owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
    })
    LaunchedEffect(page) { page.load() }
}
