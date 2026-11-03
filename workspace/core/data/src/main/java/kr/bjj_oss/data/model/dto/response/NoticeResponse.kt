package kr.bjj_oss.data.model.dto.response

import com.squareup.moshi.JsonClass
import kr.bjj_oss.model.NoticeSettingInfo
import kr.bjj_oss.model.NoticeUnreadCountInfo

@JsonClass(generateAdapter = true)
data class NoticeUnreadCountResponse(
    override val success: Boolean?,
    override val code: String?,
    override val message: String?,
    override val data: Int?
) : ApiResponse<Int?>

@JsonClass(generateAdapter = true)
data class NoticeResponseData(
    val cnt: Int?
)

internal fun NoticeResponseData?.toInfo(): NoticeUnreadCountInfo =
    NoticeUnreadCountInfo(
        cnt = this?.cnt
    )

@JsonClass(generateAdapter = true)
data class FetchNoticeSettingResponse(
    override val success: Boolean?,
    override val code: String?,
    override val message: String?,
    override val data: FetchNoticeSettingData?
): ApiResponse<FetchNoticeSettingData>

@JsonClass(generateAdapter = true)
data class FetchNoticeSettingData(
    val securityEnabled: Boolean?,
    val serviceEnabled: Boolean?,
    val communityEnabled: Boolean?,
    val marketingEnabled: Boolean?,
    val marketingAgreedAt: String?,
)

internal fun FetchNoticeSettingData?.toInfo(): NoticeSettingInfo =
    NoticeSettingInfo(
        securityEnabled = this?.securityEnabled,
        serviceEnabled = this?.serviceEnabled,
        communityEnabled = this?.communityEnabled,
        marketingEnabled = this?.marketingEnabled,
    )