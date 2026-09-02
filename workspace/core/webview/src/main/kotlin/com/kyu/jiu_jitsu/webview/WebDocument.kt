package com.kyu.jiu_jitsu.webview

/** Main-thread state, independent of Android so stale reply and duplicate rules are testable. */
class WebDocument(val id: String) {
    @Volatile var generation: Long = 0; private set
    var alive = true; private set
    var ready = false; private set
    var backGuard = false
    private val pending = mutableListOf<NativeMessage>()
    private val requests = mutableSetOf<String>()
    val key get() = "$id:$generation"
    fun start() {
        generation++; ready = false; backGuard = false; pending.clear(); requests.clear()
    }
    fun matches(generation: Long) = alive && this.generation == generation
    fun claim(requestId: String): Boolean = alive && requests.size < 4096 && requests.add(requestId)
    fun enqueue(generation: Long, message: NativeMessage): Boolean {
        if (!matches(generation)) return false
        if (message.type.startsWith("AUTH_") && message.type != "AUTH_LOGIN_CANCELLED") {
            pending.removeAll { it.type.startsWith("AUTH_") }
        }
        if (pending.size < 128) pending.add(message)
        return ready
    }
    fun markReady(): Boolean {
        if (!alive || ready) return false
        ready = true
        return true
    }
    fun drain(): List<NativeMessage> = if (ready && alive) pending.toList().also { pending.clear() } else emptyList()
    fun destroy() { start(); alive = false }
}
