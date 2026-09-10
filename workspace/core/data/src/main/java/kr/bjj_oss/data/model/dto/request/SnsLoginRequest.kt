package kr.bjj_oss.data.model.dto.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SnsLoginRequest(
    val snsProvider: String,
    val accessToken: String? = null,
    val idToken: String? = null,
)