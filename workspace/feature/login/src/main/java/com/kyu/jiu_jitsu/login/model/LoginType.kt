package com.kyu.jiu_jitsu.login.model

/** Supported providers and their backend wire values. Credentials never belong in this enum. */
enum class LoginType(
    val type: String,
) {
    KAKAO_ACCOUNT("KAKAO"),
    GOOGLE("GOOGLE"),
    APPLE("APPLE"),
}
