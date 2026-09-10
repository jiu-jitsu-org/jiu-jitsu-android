package kr.bjj_oss.model

/** Immutable decision input used by the splash screen after startup checks finish. */
data class SplashModel(
    val bootStrapInfo: BootStrapInfo? = null,
    val autoLogin: Boolean = false,
)
