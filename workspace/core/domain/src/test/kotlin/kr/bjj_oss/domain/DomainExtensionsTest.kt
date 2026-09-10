package kr.bjj_oss.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Year

class DomainExtensionsTest {
    @Test
    fun `nickname validation normalizes equivalent unicode and removes zero width characters`() {
        assertTrue(" Ａ\u200BＢ ".isValidUserNickName())
    }

    @Test
    fun `nickname validation rejects unsupported punctuation and invalid length`() {
        assertFalse("a".isValidUserNickName())
        assertFalse("jiu-jitsu".isValidUserNickName())
    }

    @Test
    fun `weight combination is locale independent and clamps the decimal digit`() {
        assertEquals(78.5, combineToDouble(78, 5), 0.0)
        assertEquals(78.9, combineToDouble(78, 12), 0.0)
    }

    @Test
    fun `negative year range returns only the current year`() {
        assertEquals(listOf(Year.now().value), yearsDescending(-1))
    }
}
