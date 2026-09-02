package com.kyu.jiu_jitsu.webview

import java.net.URI

/** Document allowlist; resource/CDN requests are deliberately outside this policy. */
class WebUrlPolicy(origin: String, private val debug: Boolean = false) {
    private val base = parse(origin) ?: error("Invalid web origin")
    val origin: String
    init {
        require(base.rawPath.isNullOrEmpty() || base.rawPath == "/")
        require(base.rawQuery == null && base.rawFragment == null)
        require(base.scheme.equals("https", true) ||
            (debug && base.scheme.equals("http", true) && base.host in LOCAL_HOSTS))
        val scheme = base.scheme.lowercase()
        val port = port(base)
        this.origin = "$scheme://${base.host.lowercase()}" +
            if (port == if (scheme == "https") 443 else 80) "" else ":$port"
    }

    fun isTrustedDocument(url: String?): Boolean {
        val uri = parse(url) ?: return false
        return uri.scheme.equals(base.scheme, true) && uri.host.equals(base.host, true) &&
            port(uri) == port(base) && isScreenPath(uri.rawPath.orEmpty())
    }

    fun isExternalWebUrl(url: String): Boolean {
        val uri = parse(url) ?: return false
        return uri.scheme.lowercase() in setOf("http", "https") && !sameOrigin(uri)
    }

    fun needsNativeClose(url: String): Boolean =
        parse(url)?.path?.startsWith("/community/") != true

    private fun sameOrigin(uri: URI) = uri.scheme.equals(base.scheme, true) &&
        uri.host.equals(base.host, true) && port(uri) == port(base)

    private fun isScreenPath(path: String): Boolean = path in setOf(
        "", "/", "/community/write", "/policies", "/policies/terms-of-service",
        "/policies/privacy-policy", "/policies/marketing-consent", "/service-info", "/version-info",
    ) || Regex("/community/[1-9][0-9]*").matches(path)

    companion object {
        private val LOCAL_HOSTS = setOf("localhost", "127.0.0.1", "10.0.2.2")
        private fun parse(value: String?): URI? = runCatching {
            require(!value.isNullOrBlank() && value.none { it.isWhitespace() || it == '\\' })
            URI(value).also {
                require(it.isAbsolute && !it.isOpaque && !it.host.isNullOrBlank())
                require(it.rawUserInfo == null && it.port in -1..65535 && it.port != 0)
            }
        }.getOrNull()
        private fun port(uri: URI): Int = if (uri.port != -1) uri.port else
            if (uri.scheme.equals("https", true)) 443 else 80
    }
}
