package kr.bjj_oss.data.repository

import kr.bjj_oss.model.AppResult

/** Prepares WebView's first request; transport cookies never escape the data boundary. */
interface WebSessionRepository {
    val origin: String
    val debuggingEnabled: Boolean
    suspend fun prepare(): AppResult<String?>
}
