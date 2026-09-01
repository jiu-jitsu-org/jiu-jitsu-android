package com.kyu.jiu_jitsu.data.model.dto.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckNicknameResponse(
    override val success: Boolean?,
    override val code: String?,
    override val message: String?,
    override val data: Boolean?,
) : ApiResponse<Boolean>
