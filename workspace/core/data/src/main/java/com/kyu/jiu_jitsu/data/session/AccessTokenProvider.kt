package com.kyu.jiu_jitsu.data.session

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thread-safe in-memory token view used by OkHttp interceptors.
 *
 * The encrypted DataStore remains the durable source. This provider exists because an OkHttp
 * interceptor is synchronous and must not block on disk for every request. Only the session data
 * source and refresh interceptor are allowed to mutate it.
 */
@Singleton
class AccessTokenProvider @Inject constructor() {
    @Volatile
    private var accessToken: String? = null

    fun current(): String? = accessToken

    fun update(value: String?) {
        accessToken = value?.takeIf(String::isNotBlank)
    }
}
