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
    private var state = SessionHeader(null, 0)

    fun current(): String? = state.accessToken
    fun snapshot(): SessionHeader = state

    fun update(value: String?, revision: Long) {
        state = SessionHeader(value?.takeIf(String::isNotBlank), revision)
    }
}

/** Immutable header snapshot binds the credential and account revision atomically. */
class SessionHeader(val accessToken: String?, val revision: Long)
class SessionRequestRevision(val value: Long)
