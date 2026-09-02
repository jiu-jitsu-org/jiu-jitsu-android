package com.kyu.jiu_jitsu.data.repository

import com.kyu.jiu_jitsu.model.AppResult

/** Prepares WebView's first request; transport cookies never escape the data boundary. */
interface WebSessionRepository {
    val origin: String
    val debuggingEnabled: Boolean
    suspend fun prepare(): AppResult<String?>
}
