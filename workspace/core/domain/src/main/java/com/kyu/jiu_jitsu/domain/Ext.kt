package com.kyu.jiu_jitsu.domain

// 허용 문자: 영문/숫자/한글(가-힣) 만, 길이 2~12자
private val ID_REGEX = Regex("^[A-Za-z0-9가-힣]{2,12}$")

/** 유니코드 정규화 + 제어/Zero-width 문자 제거 */
fun normalizeId(input: String): String {
    val nfc = java.text.Normalizer.normalize(input.trim(), java.text.Normalizer.Form.NFKC)
    // zero-width 스페이스/조인너 제거
    return nfc.replace(Regex("[\\u200B-\\u200D\\uFEFF]"), "")
}

/** 최종 검증: 규칙에 맞으면 true (허용 문자: 영문/숫자/한글(가-힣) 만, 길이 2~12자) */
fun String.isValidUserNickName(): Boolean = ID_REGEX.matches(normalizeId(this))
