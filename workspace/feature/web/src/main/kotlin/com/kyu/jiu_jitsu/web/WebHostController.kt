package com.kyu.jiu_jitsu.web

import android.content.Context
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import androidx.compose.runtime.*
import com.kyu.jiu_jitsu.model.AppResult
import com.kyu.jiu_jitsu.webview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class ReplyTarget(val page: WebViewPage, val generation: Long) {
    val key get() = "${page.document.id}:$generation"
    fun send(message: NativeMessage) = page.send(generation, message)
    fun alive() = page.isCurrent(generation)
}
internal sealed interface WebSurface {
    val target: ReplyTarget
    class Confirm(override val target: ReplyTarget, val request: ConfirmRequest) : WebSurface
    class Select(override val target: ReplyTarget, val request: SelectRequest) : WebSurface
    class Login(override val target: ReplyTarget, val reason: String?) : WebSurface
}
internal class FileRequest(val target: ReplyTarget, val callback: ValueCallback<Array<Uri>>, val multiple: Boolean)
internal class WebEntry(val page: WebViewPage, val presentation: String)

/** Composition-owned orchestration; the data layer owns tokens and HTTP. */
internal class WebHostController(
    private val context: Context,
    val policy: WebUrlPolicy,
    private val initialUrl: String,
    private val model: WebViewModel,
    private val scope: CoroutineScope,
    private val external: (String) -> Unit,
    private val share: (String, String?) -> Unit,
    private val chooseFile: (FileRequest) -> Unit,
) {
    val entries = mutableStateListOf<WebEntry>()
    val top get() = entries.lastOrNull()?.page
    var preparing by mutableStateOf(true); private set
    var preparationFailed by mutableStateOf(false); private set
    var surface by mutableStateOf<WebSurface?>(null); private set
    var loginVisible by mutableStateOf(false); private set
    var fileRequest: FileRequest? = null; private set
    private val loginTargets = linkedMapOf<String, ReplyTarget>()
    private val lastTokens = mutableMapOf<String, String?>()
    private val refreshing = mutableSetOf<String>()
    private var token: String? = null
    private var disposed = false
    private var preparation: Job? = null
    private var surfaceTimeout: Job? = null

    fun prepare() {
        preparation?.cancel()
        preparation = scope.launch {
            preparing = true
            preparationFailed = false
            val revision = model.revision()
            val result = model.prepare()
            if (disposed || model.revision() != revision) return@launch
            preparing = false
            when (result) {
                is AppResult.Success -> {
                    token = result.data
                    if (entries.isEmpty()) {
                        open("${policy.origin}/")
                        if (initialUrl != "${policy.origin}/") open(initialUrl)
                    }
                }
                is AppResult.Failure -> preparationFailed = true
            }
        }
    }

    fun sessionChanged(newAccessToken: String?) {
        // An anonymous web-initiated login must keep requireAuth's pending action alive.
        if (loginVisible && token == null && !newAccessToken.isNullOrBlank()) return
        resetPages()
        prepare()
    }

    fun retry(page: WebViewPage) {
        val index = entries.indexOfFirst { it.page == page }
        if (index < 0) return
        val url = page.currentUrl
        val style = entries[index].presentation
        // 다시 시도로 교체된 자식도 최초 OPEN_SUBVIEW의 보조 초기화 정책을 유지한다.
        val initializeOnFinished = page.initializeBridgeOnPageFinished
        entries.removeAt(index)
        page.destroy()
        entries.add(index, createEntry(url, style, initializeOnFinished))
    }

    private fun createEntry(url: String, style: String, initializeOnFinished: Boolean = false): WebEntry = WebEntry(
        WebViewPage(context, policy, url, model.debuggingEnabled, ::message, ::documentEnded, external, ::file,
            initializeBridgeOnPageFinished = initializeOnFinished), style,
    )

    private fun open(url: String, style: String = "push", initializeOnFinished: Boolean = false) {
        if (!policy.isTrustedDocument(url) || entries.size >= 8) return
        // 부모 page에는 loadUrl/reload를 호출하지 않는다. 새 entry가 자신만의 WebView,
        // 문서 ID, READY 상태와 응답 큐를 가지며 기존 부모는 entries 안에 그대로 남는다.
        // 실제 최초 로드는 WebPageView의 LaunchedEffect(page)가 한 번 수행한다.
        entries.add(createEntry(url, style, initializeOnFinished))
    }

    /**
     * 네이티브 상단 버튼과 시스템 Back의 공통 탐색 경로. IME 처리는 Route가 먼저 수행한다.
     * BACK_GUARD가 처리 중이면 웹 응답을 기다리며 goBack/pop을 중복 실행하지 않는다.
     * 정상 문서는 자신의 브라우저 히스토리를 먼저 소비하고, 없을 때만 서브 화면을 닫는다.
     * 실패/renderer 종료 문서는 WebView 메서드 호출 대신 닫아 부모로 복귀할 수 있게 한다.
     */
    fun back() {
        if (surface != null) { dismissSurface(); return }
        if (loginVisible) { completeLogin(false); return }
        val page = top ?: return
        if (page.requestBack()) return
        if (!page.failed && page.view.canGoBack()) page.view.goBack()
        else if (entries.size > 1) close(page)
    }

    fun canHandleBack(): Boolean = surface != null || loginVisible || entries.size > 1 ||
        top?.let { (!it.failed && it.backGuard) || it.canGoBack } == true

    private fun close(page: WebViewPage) {
        if (page != top || entries.size <= 1) return
        // CLOSE_SUBVIEW는 웹이 명시적으로 닫기를 결정한 메시지이므로 히스토리와 무관하다.
        // 최상단 자식만 해제하며, 부모는 재로드 없이 같은 인스턴스로 다시 표시된다.
        entries.removeAt(entries.lastIndex)
        page.destroy()
    }

    private fun message(page: WebViewPage, generation: Long, message: WebMessage) {
        if (disposed) return
        val target = ReplyTarget(page, generation)
        when (message) {
            WebMessage.Ready -> if (target.key !in refreshing && target.key !in lastTokens) {
                scope.launch {
                    val epoch = model.revision()
                    val access = model.token()
                    if (model.revision() == epoch && target.alive()) sendAuth(target, access)
                }
            }
            is WebMessage.Login -> {
                if (page != top) { target.send(NativeMessage.cancelled); return }
                loginTargets[target.key] = target
                if (loginVisible || surface is WebSurface.Login) return
                if (surface != null) { loginTargets.remove(target.key); target.send(NativeMessage.cancelled); return }
                if (message.prompt) showSurface(WebSurface.Login(target, message.reason)) else startLogin()
            }
            WebMessage.Refresh -> if (refreshing.add(target.key)) scope.launch {
                val epoch = model.revision()
                val result = model.refresh(lastTokens[target.key] ?: token)
                refreshing.remove(target.key)
                if (disposed || model.revision() != epoch || !target.alive()) return@launch
                when (result) {
                    is AppResult.Success -> { token = result.data; sendAuth(target, result.data) }
                    is AppResult.Failure -> target.send(NativeMessage.expired)
                }
            }
            WebMessage.Logout -> scope.launch {
                entries.forEach { it.page.send(it.page.document.generation, NativeMessage.logout) }
                model.logout()
            }
            is WebMessage.Open -> if (page == top && surface == null && !loginVisible) open(message.url, message.presentation, initializeOnFinished = true)
            WebMessage.Close -> close(page)
            is WebMessage.BackGuard -> Unit // Runtime updates this even before READY.
            is WebMessage.Confirm -> if (page.document.claim(message.request.requestId)) {
                if (page != top || surface != null || loginVisible) target.send(NativeMessage.confirm(message.request.requestId, ConfirmResult.DISMISS))
                else showSurface(WebSurface.Confirm(target, message.request))
            }
            is WebMessage.Select -> if (page.document.claim(message.request.requestId)) {
                if (page != top || surface != null || loginVisible) target.send(NativeMessage.select(message.request.requestId))
                else showSurface(WebSurface.Select(target, message.request))
            }
            is WebMessage.RejectedDialog -> if (page.document.claim(message.requestId)) {
                target.send(if (message.select) NativeMessage.select(message.requestId) else NativeMessage.confirm(message.requestId, ConfirmResult.DISMISS))
            }
            is WebMessage.Share -> if (page == top && policy.isTrustedDocument(message.url)) share(message.url, message.title)
        }
    }

    private fun sendAuth(target: ReplyTarget, access: String?) {
        lastTokens[target.key] = access
        target.send(if (access.isNullOrBlank()) NativeMessage.logout else NativeMessage.login(access))
    }

    fun startLogin() {
        surfaceTimeout?.cancel()
        surface = null
        if (loginVisible) return
        loginVisible = true
    }

    /** The native login screen's skip/back uses this too; durable token determines the result. */
    fun completeLogin(success: Boolean) {
        if (!loginVisible) return
        loginVisible = false
        val targets = loginTargets.values.toList()
        loginTargets.clear()
        scope.launch {
            val epoch = model.revision()
            val access = model.token()
            if (disposed || model.revision() != epoch) return@launch
            // If a pre-existing account changed outside this flow, its views are reset by revision observation.
            token = access
            targets.filter { it.alive() }.forEach {
                if (!success || access.isNullOrBlank()) it.send(NativeMessage.cancelled) else sendAuth(it, access)
            }
        }
    }

    private fun showSurface(value: WebSurface) {
        surface = value
        surfaceTimeout?.cancel()
        surfaceTimeout = scope.launch { delay(295_000); if (surface === value) dismissSurface() }
    }

    fun confirm(result: ConfirmResult) {
        val shown = surface as? WebSurface.Confirm ?: return
        surface = null
        surfaceTimeout?.cancel()
        shown.target.send(NativeMessage.confirm(shown.request.requestId, result))
    }

    fun select(value: String?, text: String?) {
        val shown = surface as? WebSurface.Select ?: return
        if (value != null && shown.request.options.none { it.value == value }) return
        surface = null
        surfaceTimeout?.cancel()
        val custom = text?.takeIf { shown.request.options.any { it.value == value && it.allowsCustomText } }
        shown.target.send(NativeMessage.select(shown.request.requestId, value, custom))
    }

    fun dismissSurface() {
        when (surface) {
            is WebSurface.Confirm -> confirm(ConfirmResult.DISMISS)
            is WebSurface.Select -> select(null, null)
            is WebSurface.Login -> {
                surface = null
                surfaceTimeout?.cancel()
                loginTargets.values.forEach { it.send(NativeMessage.cancelled) }
                loginTargets.clear()
            }
            null -> Unit
        }
    }

    private fun file(page: WebViewPage, generation: Long, callback: ValueCallback<Array<Uri>>, params: WebChromeClient.FileChooserParams) {
        finishFile(null)
        val imageOnly = params.acceptTypes.filter { it.isNotBlank() }.all { it.startsWith("image/") }
        if (!imageOnly || params.isCaptureEnabled || params.mode !in setOf(WebChromeClient.FileChooserParams.MODE_OPEN, WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE)) {
            callback.onReceiveValue(null); return
        }
        val request = FileRequest(ReplyTarget(page, generation), callback, params.mode == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE)
        fileRequest = request
        chooseFile(request)
    }

    fun finishFile(uris: Array<Uri>?, expected: FileRequest? = fileRequest) {
        val request = fileRequest ?: return
        if (request !== expected) return
        fileRequest = null
        request.callback.onReceiveValue(uris?.takeIf { request.target.alive() })
    }

    private fun documentEnded(page: WebViewPage) {
        if (surface?.target?.page == page) { surface = null; surfaceTimeout?.cancel() }
        if (fileRequest?.target?.page == page) finishFile(null)
        loginTargets.entries.removeAll { it.value.page == page }
        lastTokens.keys.removeAll { it.startsWith("${page.document.id}:") }
        refreshing.removeAll { it.startsWith("${page.document.id}:") }
    }

    private fun resetPages() {
        surface = null
        surfaceTimeout?.cancel()
        loginVisible = false
        loginTargets.clear()
        val old = entries.toList()
        entries.clear()
        old.forEach { it.page.destroy() }
        token = null
    }

    fun destroy() {
        disposed = true
        preparation?.cancel()
        resetPages()
        finishFile(null)
    }
}
