package com.kyu.jiu_jitsu.domain

import java.time.Year
import java.text.Normalizer

// The backend currently accepts ASCII letters, digits, and complete Hangul syllables only.
private val NICKNAME_REGEX = Regex("^[A-Za-z0-9가-힣]{2,12}$")
private val ZERO_WIDTH_CHARACTERS = Regex("[\\u200B-\\u200D\\uFEFF]")

/** Normalizes equivalent Unicode forms and removes invisible characters used to spoof names. */
fun normalizeId(input: String): String =
    Normalizer.normalize(input.trim(), Normalizer.Form.NFKC)
        .replace(ZERO_WIDTH_CHARACTERS, "")

/** Returns true when the normalized nickname satisfies the current product contract. */
fun String.isValidUserNickName(): Boolean = NICKNAME_REGEX.matches(normalizeId(this))

/** Combines a whole-number weight and one decimal digit without locale-sensitive formatting. */
fun combineToDouble(
    a: Int,
    b: Int,
): Double = a + b.coerceIn(0, 9) / 10.0

/** Returns the current year followed by [lastIndex] prior years in descending order. */
fun yearsDescending(lastIndex: Int): List<Int> {
    val currentYear = Year.now().value
    return (currentYear downTo (currentYear - lastIndex.coerceAtLeast(0))).toList()
}
