package kr.bjj_oss.data.model.dto.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateNoticeSettingRequest(
    val securityEnabled: Boolean?,
    val serviceEnabled: Boolean?,
    val communityEnabled: Boolean?,
    val marketingEnabled: Boolean?,
)
