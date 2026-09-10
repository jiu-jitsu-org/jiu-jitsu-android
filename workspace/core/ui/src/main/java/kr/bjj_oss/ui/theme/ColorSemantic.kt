package kr.bjj_oss.ui.theme

import androidx.compose.ui.graphics.Color

object ColorSemantic {

    val PrimaryTextSubtle: Color = Blue500
    val Error: Color = Red500
    val OnError: Color = White

    object Text {
        val TextPrimary: Color = CoolGray900
        val TextSecondary: Color = CoolGray500
        val TextTertiary: Color = CoolGray300
        val OnOverlay: Color = White
        val TextOnPrimary: Color = White
        val TextOnDark: Color = White
        val TextDisabled: Color = CoolGray400
        val TextOnInactive: Color = White
    }

    object Icon {
        val IconPrimary: Color = CoolGray900
        val IconSecondary: Color = CoolGray500
        val IconTertiary: Color = CoolGray300
        val IconSubtle: Color = CoolGray100
        val IconOnDarkSubtle: Color = WhiteOpacity60
        val OnOverlay: Color = White
        val IconOnPrimary: Color = White
        val IconOnDark: Color = White
        val IconDisabled: Color = CoolGray400
        val IconDisabledOnDark: Color = WhiteOpacity40
        val IconOnInactive: Color = White
        val IconOnInactive2: Color = White
    }

    object Transparent {
        val Transparent: Color = Color(0x00FFFFFF)
    }

    object Border {
        val BorderDefault: Color = CoolGray200
        val BorderPressed: Color = CoolGray300
        val BorderDisabled: Color = CoolGray100
        val BorderSubtle: Color = CoolGray25
        val BorderSubtle2: Color = CoolGray25
        val BorderFocus: Color = Blue500
        val BorderError: Color = Red500
    }

    object Overlay {
        val OverlayContainer: Color = CoolGray50
        val OverlayContainerPressed: Color = CoolGray200
        val OverlayTextPrimary: Color = CoolGray900
        val OverlayBorder: Color = CoolGray200
        val OverlayContainerDisabled: Color = CoolGray100
        val OverlayTextDisabled: Color = CoolGray400
        val OverlayScrim: Color = BlackOpacity40
        val OverlayScrimHeavy: Color = BlackOpacity80
        val OverlaySurface: Color = CoolGray700
        val OverlaySurfacePressed: Color = CoolGray800
    }

    object Primary {
        val Primary: Color = Blue500
        val PrimaryPressed: Color = Blue600
        val PrimaryFaint: Color = Blue50
        val PrimarySubtle: Color = Blue100
        val OnPrimary: Color = White

        // Compatibility alias for the previous Android token shape.
        val TextSubtle: Color = PrimaryTextSubtle
    }

    object Surface {
        val SurfaceContainer: Color = White
        val SurfaceFieldSubtle: Color = CoolGray10
        val OnDarkPressedBg: Color = WhiteOpacity20
        val SurfaceContainerPressed: Color = CoolGray75
        val SurfaceContainerDisabled: Color = CoolGray100
        val SurfaceSecondary: Color = CoolGray50
        val SurfaceSecondaryPressed: Color = CoolGray75
        val SurfaceTertiary: Color = CoolGray75
        val SurfacePrimary: Color = Blue100
        val SurfacePrimaryPressed: Color = Blue200
        val SurfacePrimarySubtle: Color = Blue50
        val SurfacePrimarySubtlePressed: Color = Blue75
        val SurfacePrimarySubtleStrong: Color = Blue75
        val SurfacePrimarySubtleStrongPressed: Color = Blue100
        val SurfaceBackground: Color = CoolGray25
        val SurfaceInactive: Color = CoolGray75
        val SurfaceDisabled: Color = CoolGray50
        val OverlaySurface: Color = CoolGray700
        val OverlaySurfacePressed: Color = CoolGray800
        val SurfaceField: Color = CoolGray25
        val BackgroundDefault: Color = CoolGray25
        val SurfacePollA: Color = RedBalance50
        val SurfacePollATrack: Color = RedBalance100
        val SurfacePollAStrong: Color = RedBalance500
        val SurfacePollB: Color = BlueBalance50
        val SurfacePollBTrack: Color = BlueBalance100
        val SurfacePollBStrong: Color = BlueBalance500
        val SurfaceDestructive: Color = Red500

        // Compatibility aliases for the previous Android token names.
        val OverLaySurface: Color = OverlaySurface
        val OverLaySurfacePressed: Color = OverlaySurfacePressed
    }

    object Interactive {
        val InteractivePrimary: Color = Blue500
        val InteractivePrimaryPressed: Color = Blue600
        val InteractiveDisabled: Color = CoolGray100
        val InteractiveStrong: Color = CoolGray700
        val InteractiveStrongPressed: Color = CoolGray800
        val InteractivePollA: Color = RedBalance500
        val InteractivePollB: Color = BlueBalance500

        // Compatibility alias for the previous Android token shape.
        val InteractivePrimaryDisabled: Color = InteractiveDisabled
    }

    object Destructive {
        val Destructive: Color = Red500
    }
}
