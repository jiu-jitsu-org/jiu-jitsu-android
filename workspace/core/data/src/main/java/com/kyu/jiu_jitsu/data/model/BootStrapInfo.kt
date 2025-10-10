package com.kyu.jiu_jitsu.data.model

data class BootStrapInfo(
    val appVersionInfo: AppVersionInfo?,
)

data class AppVersionInfo(
    val minVersion: String,
    val nowVersion: String,
    val needForceUpdate: Boolean,
)