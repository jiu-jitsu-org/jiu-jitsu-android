package com.kyu.jiu_jitsu.webview

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

sealed interface WebMessage {
    data object Ready : WebMessage
    data class Login(val prompt: Boolean, val reason: String?) : WebMessage
    data object Logout : WebMessage
    data object Refresh : WebMessage
    data class Open(val url: String, val title: String?, val presentation: String) : WebMessage
    data object Close : WebMessage
    data class BackGuard(val enabled: Boolean) : WebMessage
    data class Confirm(val request: ConfirmRequest) : WebMessage
    data class Select(val request: SelectRequest) : WebMessage
    data class Share(val url: String, val title: String?) : WebMessage
    data class RejectedDialog(val requestId: String, val select: Boolean) : WebMessage
}

@Serializable
data class ConfirmRequest(
    val requestId: String, val title: String, val confirmText: String,
    val message: String? = null, val cancelText: String? = null,
    val destructive: Boolean = false, val dismissOnOutsideTap: Boolean = true,
)
@Serializable
data class SelectRequest(
    val requestId: String, val title: String, val options: List<SelectOption>,
    val submitText: String, val message: String? = null, val customTextPlaceholder: String? = null,
)
@Serializable
data class SelectOption(val value: String, val label: String, val allowsCustomText: Boolean = false)

enum class ConfirmResult(val wire: String) { CONFIRM("confirm"), CANCEL("cancel"), DISMISS("dismiss") }

/** The only seven native-to-web message names in the current web contract. */
class NativeMessage private constructor(internal val type: String, private val payload: JsonObject? = null) {
    internal fun json(): String = buildJsonObject {
        put("type", type)
        payload?.let { put("payload", it) }
    }.toString()
    override fun toString(): String = type // Never expose a token via diagnostic interpolation.
    companion object {
        fun login(token: String) = NativeMessage("AUTH_LOGIN_SUCCESS", buildJsonObject { put("accessToken", token) })
        val cancelled get() = NativeMessage("AUTH_LOGIN_CANCELLED")
        val expired get() = NativeMessage("AUTH_SESSION_EXPIRED")
        val logout get() = NativeMessage("AUTH_LOGOUT")
        val back get() = NativeMessage("BACK_PRESSED")
        fun confirm(id: String, result: ConfirmResult) = NativeMessage("CONFIRM_DIALOG_RESULT", buildJsonObject {
            put("requestId", id); put("result", result.wire)
        })
        fun select(id: String, value: String? = null, customText: String? = null) =
            NativeMessage("SELECT_SHEET_RESULT", buildJsonObject {
                put("requestId", id); put("result", if (value == null) "dismiss" else "submit")
                value?.let { put("value", it) }; customText?.let { put("customText", it) }
            })
    }
}

object BridgeCodec {
    private val json = Json { ignoreUnknownKeys = true }
    fun decode(raw: String): WebMessage? = runCatching {
        require(raw.length <= 65536)
        val obj = json.parseToJsonElement(raw).jsonObject
        val type = obj.requiredString("type")
        val p = obj["payload"]?.jsonObject ?: JsonObject(emptyMap())
        when (type) {
            "WEBVIEW_READY" -> WebMessage.Ready
            "AUTH_LOGIN_PROMPT", "AUTH_LOGIN_MODAL" -> WebMessage.Login(type == "AUTH_LOGIN_PROMPT", p.optionalString("reason"))
            "AUTH_LOGOUT_REQUEST" -> WebMessage.Logout
            "AUTH_TOKEN_REFRESH_REQUEST" -> WebMessage.Refresh
            "CLOSE_SUBVIEW" -> WebMessage.Close
            "BACK_GUARD" -> WebMessage.BackGuard(p.getValue("enabled").jsonPrimitive.let {
                require(!it.isString); requireNotNull(it.booleanOrNull)
            })
            "OPEN_SUBVIEW" -> WebMessage.Open(p.requiredString("url"), p.optionalString("title"),
                (p.optionalString("presentation") ?: "push").also { require(it in setOf("push", "modal")) })
            "SHOW_SHARE_SHEET" -> WebMessage.Share(p.requiredString("url"), p.optionalString("title"))
            "SHOW_CONFIRM_DIALOG" -> WebMessage.Confirm(json.decodeFromJsonElement<ConfirmRequest>(p.also { validateConfirm(it) }).also {
                require(it.requestId.isNotBlank() && it.title.isNotBlank() && it.confirmText.isNotBlank())
            })
            "SHOW_SELECT_SHEET" -> WebMessage.Select(json.decodeFromJsonElement<SelectRequest>(p.also { validateSelect(it) }).also {
                require(it.requestId.isNotBlank() && it.title.isNotBlank() && it.submitText.isNotBlank())
                require(it.options.size in 1..100 && it.options.all { o -> o.value.isNotBlank() && o.label.isNotBlank() })
                require(it.options.map { o -> o.value }.distinct().size == it.options.size)
            })
            else -> null
        }
    }.getOrElse { rejectedDialog(raw) }

    private fun rejectedDialog(raw: String): WebMessage? = runCatching {
        if (raw.length > 65536) return null
        val obj = json.parseToJsonElement(raw).jsonObject
        val type = obj.requiredString("type")
        if (type !in setOf("SHOW_CONFIRM_DIALOG", "SHOW_SELECT_SHEET")) return null
        WebMessage.RejectedDialog(obj.getValue("payload").jsonObject.requiredString("requestId"), type == "SHOW_SELECT_SHEET")
    }.getOrNull()

    private fun validateConfirm(p: JsonObject) {
        listOf("requestId", "title", "confirmText").forEach { p.requiredString(it) }
        listOf("message", "cancelText").forEach { p.optionalString(it) }
        listOf("destructive", "dismissOnOutsideTap").forEach { p.optionalBoolean(it) }
    }
    private fun validateSelect(p: JsonObject) {
        listOf("requestId", "title", "submitText").forEach { p.requiredString(it) }
        listOf("message", "customTextPlaceholder").forEach { p.optionalString(it) }
        p.getValue("options").jsonArray.forEach {
            val option = it.jsonObject
            option.requiredString("value"); option.requiredString("label"); option.optionalBoolean("allowsCustomText")
        }
    }
    private fun JsonObject.optionalBoolean(key: String) {
        get(key)?.let { require(it is JsonPrimitive && !it.isString && it.booleanOrNull != null) }
    }

    fun script(origin: String, documentKey: String, message: NativeMessage): String =
        """(() => {
          if (window.location.origin !== ${literal(origin)}) return false;
          if (window.__ossNativeDocument !== ${literal(documentKey)}) return false;
          if (typeof window.WebBridge?.receive !== "function") return false;
          window.WebBridge.receive(${literal(message.json())});
          return true;
        })();""".trimIndent()

    internal fun literal(value: String) = JsonPrimitive(value).toString()
        .replace("\u2028", "\\u2028").replace("\u2029", "\\u2029")
    private fun JsonObject.requiredString(key: String): String = getValue(key).jsonPrimitive.let {
        require(it.isString && it.content.isNotBlank()); it.content
    }
    private fun JsonObject.optionalString(key: String): String? = get(key)?.let {
        require(it is JsonPrimitive && it.isString); it.content
    }
}
