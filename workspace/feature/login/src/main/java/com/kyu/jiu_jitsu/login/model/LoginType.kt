package com.kyu.jiu_jitsu.login.model

sealed class LoginType(
    val type: String,
    var snsLoginToken: String? = null,
) {
    data object KAKAO_ACCOUNT : LoginType("KAKAO")
    data object GOOGLE : LoginType("GOOGLE")
    data object APPLE : LoginType("APPLE")
}