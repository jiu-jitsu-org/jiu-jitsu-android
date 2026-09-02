package com.kyu.jiu_jitsu.model

/** Immutable decision input used by the splash screen after startup checks finish. */
data class SplashModel(
    val bootStrapInfo: BootStrapInfo? = null,
    val autoLogin: Boolean = false,
)
