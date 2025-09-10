package com.kyu.jiu_jitsu.data.api.common

import com.squareup.moshi.JsonClass

// 에러 포맷: { "error": "..." }
@JsonClass(generateAdapter = true)
data class ServerError(val error: String?)