package kr.bjj_oss.setting.model

/** null indicates that the persisted session has not been read yet. */
internal data class SettingUiState(
    val isLoggedIn: Boolean? = null,
    val isLoggingOut: Boolean = false,
    val logoutFailed: Boolean = false,
)
