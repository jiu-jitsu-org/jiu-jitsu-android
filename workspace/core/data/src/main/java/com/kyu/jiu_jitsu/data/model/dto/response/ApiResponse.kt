package com.kyu.jiu_jitsu.data.model.dto.response

interface ApiResponse<out T> {
    val success: Boolean?
    val code: String?
    val message: String?
    val data: T?
}
