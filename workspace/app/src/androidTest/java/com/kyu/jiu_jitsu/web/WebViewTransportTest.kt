package com.kyu.jiu_jitsu.web

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kyu.jiu_jitsu.MainActivity
import com.kyu.jiu_jitsu.webview.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.net.InetAddress
import java.net.ServerSocket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/** Runs the real @JavascriptInterface/evaluateJavascript path without server writes or credentials. */
@RunWith(AndroidJUnit4::class)
class WebViewTransportTest {
    @Test fun readyQueueAndQuotedReplyRoundTripInPlatformWebView() {
        val server = ServerSocket(0, 1, InetAddress.getByName("127.0.0.1"))
        val origin = "http://127.0.0.1:${server.localPort}"
        val html = """
            <html><body><script>
            window.WebBridge = { receive: input => { window.reply = JSON.parse(input); } };
            AppBridge.postMessage(JSON.stringify({type:'BACK_GUARD',payload:{enabled:true}}));
            AppBridge.postMessage(JSON.stringify({type:'WEBVIEW_READY'}));
            AppBridge.postMessage(JSON.stringify({type:'WEBVIEW_READY'}));
            </script></body></html>
        """.trimIndent().toByteArray(Charsets.UTF_8)
        val worker = Thread {
            runCatching {
                server.accept().use { socket ->
                    socket.soTimeout = 5000
                    val reader = socket.getInputStream().bufferedReader()
                    while (!reader.readLine().isNullOrEmpty()) { /* consume request headers */ }
                    socket.getOutputStream().apply {
                        write("HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: ${html.size}\r\nConnection: close\r\n\r\n".toByteArray())
                        write(html); flush()
                    }
                }
            }
        }.apply { isDaemon = true; start() }
        try {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            val ready = CountDownLatch(1)
            val readyCount = AtomicInteger()
            lateinit var page: WebViewPage
            scenario.onActivity { activity ->
                page = WebViewPage(activity, WebUrlPolicy(origin, true), "$origin/", true,
                    onMessage = { source, generation, message ->
                        if (message is WebMessage.BackGuard) {
                            source.send(generation, NativeMessage.select("fixture", "OTHER", "quote'\"\\\n한글"))
                        }
                        if (message == WebMessage.Ready) { readyCount.incrementAndGet(); ready.countDown() }
                    }, onDocumentEnded = {}, onExternalUrl = {}, onChooseFile = { _, _, cb, _ -> cb.onReceiveValue(null) })
                activity.setContentView(page.view)
                page.load()
            }
            assertTrue("Real JS bridge should become ready", ready.await(15, TimeUnit.SECONDS))
            val delivered = CountDownLatch(1)
            var result: String? = null
            scenario.onActivity {
                assertTrue(page.backGuard)
                page.view.evaluateJavascript("window.reply?.type + ':' + window.reply?.payload?.requestId + ':' + window.reply?.payload?.customText?.includes('한글')") {
                    result = it; delivered.countDown()
                }
            }
            assertTrue(delivered.await(5, TimeUnit.SECONDS))
            assertEquals("\"SELECT_SHEET_RESULT:fixture:true\"", result)
            assertEquals(1, readyCount.get())
            scenario.onActivity { page.destroy() }
        }
        } finally { server.close(); worker.join(1000) }
    }
}
