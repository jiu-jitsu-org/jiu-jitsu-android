package kr.bjj_oss.ui.theme

import androidx.compose.ui.graphics.toArgb
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ColorTokensTest {
    @Test
    fun tokenClassNamesAreUniqueIgnoringCase() {
        val classesByName = mutableMapOf<String, String>()

        fun checkClass(tokenClass: Class<*>) {
            val previous = classesByName.put(tokenClass.name.lowercase(Locale.ROOT), tokenClass.name)
            assertNull(
                "Token classes must not collide on case-insensitive filesystems: $previous and ${tokenClass.name}",
                previous,
            )
            tokenClass.declaredClasses.forEach(::checkClass)
        }

        checkClass(ColorSemantic::class.java)
        checkClass(ColorComponents::class.java)
    }

    @Test
    fun transparentComponentBackgroundsInitialize() {
        val backgrounds = listOf(
            ColorComponents.Button.Text.DefaultBg,
            ColorComponents.Button.Text.DisabledBg,
            ColorComponents.Button.Inverted.DefaultBg,
            ColorComponents.Button.Inverted.DisabledBg,
            ColorComponents.Button.InvertedSubtle.DefaultBg,
            ColorComponents.Button.InvertedSubtle.DisabledBg,
            ColorComponents.Cta.TransparentText.Bg,
            ColorComponents.Cta.TransparentText.DisabledBg,
            ColorComponents.Segment.UnSelected.Bg,
            ColorComponents.TagChip.Default.Bg,
            ColorComponents.TagChip.Selected.Bg2,
            ColorComponents.TagChip.Disabled.Bg,
        )

        backgrounds.forEach { background ->
            assertEquals(0x00FFFFFF, background.toArgb())
        }
    }
}
