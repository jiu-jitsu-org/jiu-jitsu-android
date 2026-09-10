package kr.bjj_oss.data.api.common

import com.squareup.moshi.JsonClass

// 에러 포맷: { "error": "..." }
@JsonClass(generateAdapter = true)
data class ServerError(val error: String?)