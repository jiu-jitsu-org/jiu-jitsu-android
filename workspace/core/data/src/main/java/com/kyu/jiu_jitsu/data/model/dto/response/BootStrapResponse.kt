package com.kyu.jiu_jitsu.data.model.dto.response

import com.kyu.jiu_jitsu.data.model.AppVersionInfo
import com.kyu.jiu_jitsu.data.model.BootStrapInfo
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
    val appVersionInfo: AppVersionInfo,
)


fun BootStrapResponseData?.toInfo(): BootStrapInfo =
    BootStrapInfo(this?.appVersionInfo)
