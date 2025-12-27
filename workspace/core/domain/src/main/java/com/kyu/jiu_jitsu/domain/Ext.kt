package com.kyu.jiu_jitsu.domain

import java.time.Year

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

/** a+b = a.b */
fun combineToDouble(
    a: Int,
    b: Int
): Double {
    try {
        val formatted = "%d.%01d".format(a, b)
        return formatted.toDouble()
    } catch (e: Exception) {
        return 0.0
    }
}

/**
 * 현재 연도부터 과거 (Current - lastIndex)전까지
 * @param lastIndex 몇년 전 까지?
 */
fun yearsDescending(lastIndex: Int): List<Int> {
    val currentYear = Year.now().value
    return (currentYear downTo (currentYear - lastIndex)).toList()
}