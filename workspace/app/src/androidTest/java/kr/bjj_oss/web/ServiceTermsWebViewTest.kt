package kr.bjj_oss.web

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import kr.bjj_oss.MainActivity
import kr.bjj_oss.webview.WebUrlPolicy
import kr.bjj_oss.webview.WebViewPage
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.net.InetAddress
import java.net.ServerSocket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** 실제 WebView에서 READY 없는 공개 문서와 하단 여백의 스크롤 효과를 검증한다. */
@RunWith(AndroidJUnit4::class)
class ServiceTermsWebViewTest {
    @Test fun publicDocumentLoadsWithoutBridgeAndLastParagraphClearsFloatingButton() {
        val html = """
            <html><head><meta name="viewport" content="width=device-width, initial-scale=1"></head>
            <body style="margin:0"><p style="height:3000px">Terms</p><p id="last">Last paragraph</p></body></html>
        """.trimIndent().toByteArray(Charsets.UTF_8)
        val server = ServerSocket(0, 1, InetAddress.getByName("127.0.0.1"))
        val origin = "http://127.0.0.1:${server.localPort}"
        val worker = Thread {
            runCatching {
                server.accept().use { socket ->
                    socket.soTimeout = 5000
                    val reader = socket.getInputStream().bufferedReader()
                    while (!reader.readLine().isNullOrEmpty()) { /* consume headers */ }
                    socket.getOutputStream().apply {
                        write("HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: ${html.size}\r\nConnection: close\r\n\r\n".toByteArray())
                        write(html)
                        flush()
                    }
                }
            }
        }.apply { isDaemon = true; start() }
        try {
            ActivityScenario.launch(MainActivity::class.java).use { scenario ->
                lateinit var page: WebViewPage
                try {
                    scenario.onActivity { activity ->
                        val url = "$origin/policies/terms-of-service"
                        page = WebViewPage(
                            activity, WebUrlPolicy(origin, true), url, true,
                            onMessage = { _, _, _ -> fail("Read-only terms must not dispatch bridge messages") },
                            onDocumentEnded = {}, onExternalUrl = {},
                            onChooseFile = { _, _, callback, _ -> callback.onReceiveValue(null) },
                            bridgeEnabled = false,
                        )
                        activity.setContentView(page.view)
                        page.setBottomContentInset(240)
                        page.load()
                    }
                    val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10)
                    var loaded = false
                    while (!loaded && System.nanoTime() < deadline) {
                        scenario.onActivity { loaded = !page.loading }
                        if (!loaded) Thread.sleep(50)
                    }
                    assertTrue("A static document must load without WEBVIEW_READY", loaded)
                    scenario.onActivity {
                        assertFalse(page.failed)
                        assertFalse(page.document.ready)
                        page.view.evaluateJavascript("window.scrollTo(0, document.documentElement.scrollHeight)", null)
                    }
                    val measured = CountDownLatch(1)
                    var result: String? = null
                    scenario.onActivity {
                        // 강제 layout/스크롤 뒤 실제 viewport와 문단 위치를 비교한다.
                        page.view.evaluateJavascript("""
                            (() => {
                                const inset = 240 * window.innerWidth / ${page.view.width};
                                return typeof AppBridge === 'undefined' &&
                                    document.getElementById('last').getBoundingClientRect().bottom <= window.innerHeight - inset + 1;
                            })()
                        """.trimIndent()) { result = it; measured.countDown() }
                    }
                    assertTrue(measured.await(5, TimeUnit.SECONDS))
                    assertEquals("The last paragraph must scroll above the floating button", "true", result)
                } finally {
                    scenario.onActivity { page.destroy() }
                }
            }
        } finally {
            server.close()
            worker.join(1000)
        }
    }
}
