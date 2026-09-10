package kr.bjj_oss.data.model.dto.response

interface ApiResponse<out T> {
    val success: Boolean?
    val code: String?
    val message: String?
    val data: T?
}
