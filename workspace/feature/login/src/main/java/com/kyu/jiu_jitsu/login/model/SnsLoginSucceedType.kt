package com.kyu.jiu_jitsu.login.model

sealed interface SnsLoginSucceedType {
    data object SIGN_IN: SnsLoginSucceedType
    data object SIGN_UP: SnsLoginSucceedType
}