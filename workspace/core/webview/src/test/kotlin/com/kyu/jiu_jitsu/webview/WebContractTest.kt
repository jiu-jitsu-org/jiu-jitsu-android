package com.kyu.jiu_jitsu.webview

import org.junit.Assert.*
import org.junit.Test

class WebContractTest {
    private val policy = WebUrlPolicy("https://web.example.com:443/")
    @Test fun trustedRoutesAndNormalizedPort() {
        listOf("/", "/community/3", "/community/write", "/community/3?sort=oldest", "/policies/privacy-policy", "/version-info?osName=ANDROID").forEach {
            assertTrue(it, policy.isTrustedDocument("https://web.example.com$it"))
        }
        assertEquals("https://web.example.com", policy.origin)
    }
    @Test fun rejectsOriginConfusionAndUnsupportedRoutes() {
        listOf(
            "https://web.example.com.evil.test/", "https://web.example.com@evil.test/", "https://evil.test@web.example.com/",
            "http://web.example.com/", "https://web.example.com:444/", "javascript:alert(1)", "file:///etc/passwd",
            "content://images/1", "intent://open", "//web.example.com/", "https://web.example.com/api/health",
            "https://web.example.com/community", "https://web.example.com/community/0", "https://web.example.com/community/3/edit",
            "https://web.example.com/community/%33", "https://web.example.com/policies/../api/auth/session",
        ).forEach { assertFalse(it, policy.isTrustedDocument(it)) }
    }
    @Test fun localhostHttpRequiresDebug() {
        assertTrue(WebUrlPolicy("http://localhost:3000", true).isTrustedDocument("http://localhost:3000/"))
        assertTrue(runCatching { WebUrlPolicy("http://localhost:3000") }.isFailure)
        assertTrue(runCatching { WebUrlPolicy("http://example.com", true) }.isFailure)
        assertTrue(runCatching { WebUrlPolicy("https://example.com/api") }.isFailure)
    }
    @Test fun allElevenInboundContractsDecode() {
        val messages = listOf(
            """{"type":"WEBVIEW_READY"}""", """{"type":"AUTH_LOGIN_PROMPT"}""",
            """{"type":"AUTH_LOGIN_MODAL","payload":{"reason":"write"}}""", """{"type":"AUTH_LOGOUT_REQUEST"}""",
            """{"type":"AUTH_TOKEN_REFRESH_REQUEST"}""", """{"type":"CLOSE_SUBVIEW"}""",
            """{"type":"OPEN_SUBVIEW","payload":{"url":"https://web.example.com/community/3"}}""",
            """{"type":"BACK_GUARD","payload":{"enabled":true}}""",
            """{"type":"SHOW_CONFIRM_DIALOG","payload":{"requestId":"same","title":"취소","confirmText":"나가기"}}""",
            """{"type":"SHOW_SELECT_SHEET","payload":{"requestId":"same","title":"신고","submitText":"보내기","options":[{"value":"OTHER","label":"기타","allowsCustomText":true}]}}""",
            """{"type":"SHOW_SHARE_SHEET","payload":{"url":"https://web.example.com/community/3"}}""",
        )
        messages.forEach { assertNotNull(it, BridgeCodec.decode(it)) }
    }
    @Test fun strictTypesAndMalformedInput() {
        listOf("{", "[]", """{"type":1}""", """{"type":"UNKNOWN"}""",
            """{"type":"BACK_GUARD","payload":{"enabled":"true"}}""",
            """{"type":"OPEN_SUBVIEW","payload":{"url":3}}""",
            """{"type":"OPEN_SUBVIEW","payload":{"url":"https://web.example.com/","presentation":"unknown"}}""",
        ).forEach { assertNull(it, BridgeCodec.decode(it)) }
        assertTrue(BridgeCodec.decode("""{"type":"SHOW_CONFIRM_DIALOG","payload":{"requestId":"1","title":2,"confirmText":"ok"}}""") is WebMessage.RejectedDialog)
        assertTrue(BridgeCodec.decode("""{"type":"SHOW_SELECT_SHEET","payload":{"requestId":"1","title":"x","submitText":"ok","options":[]}}""") is WebMessage.RejectedDialog)
    }
    @Test fun repliesEscapeScriptInputAndKeepRequestIdInsidePayload() {
        val text = "quote'\"\\\n\u2028</script>"
        val msg = NativeMessage.select("id", "OTHER", text)
        val script = BridgeCodec.script(policy.origin, "doc:1", msg)
        assertTrue(script.contains("location.origin"))
        assertTrue(script.contains("__ossNativeDocument"))
        assertFalse(script.contains("\u2028"))
        assertTrue(msg.json().contains("\"payload\":{\"requestId\""))
        assertEquals("AUTH_LOGIN_SUCCESS", NativeMessage.login("FAKE_ONLY").toString())
        assertFalse(NativeMessage.login("FAKE_ONLY").toString().contains("FAKE_ONLY"))
    }
    @Test fun readyQueueGenerationAndGuardAreIndependent() {
        val a = WebDocument("a"); val b = WebDocument("b")
        a.start(); b.start()
        a.backGuard = true
        assertFalse(a.enqueue(a.generation, NativeMessage.login("FAKE_ONLY")))
        assertTrue(a.drain().isEmpty())
        assertTrue(a.claim("confirm-1")); assertFalse(a.claim("confirm-1")); assertTrue(b.claim("confirm-1"))
        assertTrue(a.markReady()); assertFalse(a.markReady())
        assertEquals(1, a.drain().size)
        assertTrue(a.backGuard)
        val old = a.generation
        a.start()
        assertFalse(a.backGuard); assertFalse(a.ready)
        assertFalse(a.enqueue(old, NativeMessage.cancelled))
        assertTrue(a.claim("confirm-1"))
        a.destroy(); assertFalse(a.matches(a.generation)); assertFalse(a.markReady())
    }
    @Test fun logoutReplacesQueuedLogin() {
        val doc = WebDocument("test"); doc.start()
        doc.enqueue(doc.generation, NativeMessage.login("FAKE_ONLY"))
        doc.enqueue(doc.generation, NativeMessage.logout)
        doc.markReady()
        assertEquals(listOf("AUTH_LOGOUT"), doc.drain().map { it.type })
    }
}
