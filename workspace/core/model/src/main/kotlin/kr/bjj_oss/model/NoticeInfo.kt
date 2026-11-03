package kr.bjj_oss.model

data class NoticeUnreadCountInfo(
    val cnt: Int?
)

data class NoticeSettingInfo(
    val securityEnabled: Boolean?,
    val serviceEnabled: Boolean?,
    val communityEnabled: Boolean?,
    val marketingEnabled: Boolean?,
)