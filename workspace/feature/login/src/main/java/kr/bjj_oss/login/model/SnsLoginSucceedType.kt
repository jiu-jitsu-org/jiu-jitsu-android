package kr.bjj_oss.login.model

sealed interface SnsLoginSucceedType {
    data object SIGN_IN: SnsLoginSucceedType
    data object SIGN_UP: SnsLoginSucceedType
}