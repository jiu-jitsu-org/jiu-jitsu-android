package com.kyu.jiu_jitsu.data.model.dto.response

import com.kyu.jiu_jitsu.data.model.AppVersionInfo
import com.kyu.jiu_jitsu.data.model.BootStrapInfo
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BootStrapResponse(
    val success: Boolean?,
    val code: String?,
    val message: String?,
    val data: BootStrapResponseData?,
)

@JsonClass(generateAdapter = true)
data class BootStrapResponseData(
    val appVersionInfo: AppVersionInfo,
)


fun BootStrapResponseData?.toInfo(): BootStrapInfo =
    BootStrapInfo(this?.appVersionInfo)