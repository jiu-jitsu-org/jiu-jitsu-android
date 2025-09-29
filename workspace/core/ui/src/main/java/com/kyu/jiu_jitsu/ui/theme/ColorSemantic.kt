package com.kyu.jiu_jitsu.ui.theme

import androidx.compose.ui.graphics.Color

object ColorSemantic {

    object Surface {
        val SurfaceContainer: Color = White
        val SurfaceContainerPressed: Color = CoolGray75
        val SurfaceContainerDisabled: Color = CoolGray100
        val SurfaceSecondary: Color = CoolGray50
        val SurfaceSecondaryPressed: Color = CoolGray75
        val SurfacePrimary: Color = Blue100
        val SurfacePrimaryPressed: Color = Blue200
        val SurfacePrimarySubtle: Color = Blue50
        val SurfacePrimarySubtlePressed: Color = Blue75
        val SurfaceDisabled: Color = CoolGray50
        val SurfaceBackground: Color = CoolGray25
        val SurfaceInactive: Color = CoolGray75
        val SurfaceField: Color = CoolGray25
        val OverLaySurface: Color = CoolGray700
        val OverLaySurfacePressed: Color = CoolGray800
    }

    object Primary {
        val Primary: Color = Blue500
        val PrimaryPressed: Color = Blue600
        val OnPrimary: Color = White
        val PrimaryFaint: Color = Blue50
        val PrimarySubtle: Color = Blue100
        val TextSubtle: Color = Blue500
    }

    object Text {
        val TextPrimary: Color = CoolGray900
        val TextOnPrimary: Color = White
        val TextSecondary: Color = CoolGray500
        val TextDisabled: Color = CoolGray400
        val TextTertiary: Color = CoolGray300
        val TextOnDark: Color = White
        val TextOnInactive: Color = White
        val OnOverlay: Color = White
    }

    object Overlay {
        val OverlayContainer: Color = CoolGray50
        val OverlayContainerPressed: Color = CoolGray200
        val OverlayContainerDisabled: Color = CoolGray100
        val OverlayTextPrimary: Color = CoolGray900
        val OverlayTextDisabled: Color = CoolGray400
        val OverlayBorder: Color = CoolGray200
        val OverlayScrim: Color = BlackOpacity40
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

    object Icon {
        val IconPrimary: Color = CoolGray900
        val IconOnPrimary: Color = White
        val IconSecondary: Color = CoolGray500
        val IconTertiary: Color = CoolGray300
        val IconDisabled: Color = CoolGray400
        val IconOnDark: Color = White
        val IconOnInactive: Color = White
        val IconOnInactive2: Color = White
        val OnOverlay: Color = White
    }

    object Interactive {
        val InteractivePrimary: Color = Blue500
        val InteractivePrimaryPressed: Color = Blue600
        val InteractivePrimaryDisabled: Color = CoolGray100
        val InteractiveDisabled: Color = CoolGray100
        val InteractiveStrong: Color = CoolGray700
        val InteractiveStrongPressed: Color = CoolGray800
    }

    object Error {
        val Error: Color = Red500
        val OnError: Color = White
    }

    object TransParent {
        val TransParent: Color = Color.Transparent
    }
}