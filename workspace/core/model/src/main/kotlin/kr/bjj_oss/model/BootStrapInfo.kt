package kr.bjj_oss.model

/** Application startup configuration returned by the backend. */
data class BootStrapInfo(
    val appVersionInfo: AppVersionInfo?,
)

/**
 * Version policy evaluated during startup.
 *
 * The model intentionally contains no Play Store or Android framework type. Opening an update
 * destination remains a presentation/app responsibility.
 */
data class AppVersionInfo(
    val minVersion: String,
    val nowVersion: String,
    val needForceUpdate: Boolean,
)
