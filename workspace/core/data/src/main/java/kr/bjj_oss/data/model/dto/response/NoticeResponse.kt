package kr.bjj_oss.data.model.dto.response

import com.squareup.moshi.JsonClass
import kr.bjj_oss.model.NoticeUnreadCountInfo

@JsonClass(generateAdapter = true)
data class NoticeResponse(
    override val success: Boolean?,
    override val code: String?,
    override val message: String?,
    override val data: NoticeResponseData?
) : ApiResponse<NoticeResponseData?>

@JsonClass(generateAdapter = true)
data class NoticeResponseData(
    val cnt: Int?
)

internal fun NoticeResponseData?.toInfo(): NoticeUnreadCountInfo =
    NoticeUnreadCountInfo(
        cnt = this?.cnt
    )
