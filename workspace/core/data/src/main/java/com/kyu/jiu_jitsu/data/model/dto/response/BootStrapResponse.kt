package com.kyu.jiu_jitsu.data.model.dto.response

import com.kyu.jiu_jitsu.model.AppVersionInfo
import com.kyu.jiu_jitsu.model.BootStrapInfo
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BootStrapResponse(
    override val success: Boolean?,
    override val code: String?,
    override val message: String?,
    override val data: BootStrapResponseData?,
) : ApiResponse<BootStrapResponseData>

@JsonClass(generateAdapter = true)
data class BootStrapResponseData(
    val appVersionInfo: AppVersionInfoResponse,
)

/** Wire representation kept separate so backend JSON changes do not redefine the app model. */
@JsonClass(generateAdapter = true)
data class AppVersionInfoResponse(
    val minVersion: String,
    val nowVersion: String,
    val needForceUpdate: Boolean,
)

internal fun BootStrapResponseData.toInfo(): BootStrapInfo =
    BootStrapInfo(
        appVersionInfo = AppVersionInfo(
            minVersion = appVersionInfo.minVersion,
            nowVersion = appVersionInfo.nowVersion,
            needForceUpdate = appVersionInfo.needForceUpdate,
        ),
    )
