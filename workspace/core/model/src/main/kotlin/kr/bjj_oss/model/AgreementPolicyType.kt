package kr.bjj_oss.model

/** 로그인과 웹 화면이 공유하는 약관 종류. 화면 리소스나 환경별 URL은 포함하지 않는다. */
enum class AgreementPolicyType {
    Service,
    Privacy,
    Marketing,
}
