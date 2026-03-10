package com.kyu.jiu_jitsu.nickname.model

sealed interface NickNameState {
    /** 닉네임 입력 상태 - 기본 */
    data object Idle : NickNameState
    /** 닉네임 입력 상태 - 유효성 검사 실패 */
    data object ValidationError : NickNameState
    /** 닉네임 입력 상태 - 유효성 검사 성공 */
    data object ValidationSuccess : NickNameState
    /** 닉네임 입력 상태 - 중복 검사 실패 */
    data object DuplicateError : NickNameState
    /** 닉네임 입력 상태 - 중복 검사 성공 && Sign Up */
    data object Succeed : NickNameState
    /** Sign Up Server Error */
    data class Error(val message: String, val code: Int? = null,) : NickNameState
}