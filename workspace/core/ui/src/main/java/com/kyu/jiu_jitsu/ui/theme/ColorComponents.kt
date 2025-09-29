package com.kyu.jiu_jitsu.ui.theme

import androidx.compose.ui.graphics.Color

object ColorComponents {

    object Button {
        object Filled {
            val DefaultBg: Color = ColorSemantic.Interactive.InteractivePrimary
            val DefaultText: Color = ColorSemantic.Text.TextOnPrimary
            val PressedBg: Color = ColorSemantic.Primary.PrimaryPressed
            val DisabledBg: Color = ColorSemantic.Interactive.InteractivePrimaryDisabled
            val DisabledText: Color = ColorSemantic.Text.TextDisabled
            val PressedText: Color = ColorSemantic.Text.TextOnPrimary
        }
        object Tint {
            val DefaultBg: Color = ColorSemantic.Surface.SurfacePrimarySubtle
            val DefaultText: Color = ColorSemantic.Interactive.InteractivePrimary
            val PressedBg: Color = ColorSemantic.Surface.SurfacePrimary
            val DisabledBg: Color = ColorSemantic.Surface.SurfaceDisabled
            val DisabledText: Color = ColorSemantic.Text.TextDisabled
            val PressedText: Color = ColorSemantic.Interactive.InteractivePrimary
        }
        object Neutral {
            val DefaultBg: Color = ColorSemantic.Surface.SurfaceSecondary
            val DefaultText: Color = ColorSemantic.Text.TextPrimary
            val PressedBg: Color = ColorSemantic.Surface.SurfaceSecondaryPressed
            val DisabledBg: Color = ColorSemantic.Surface.SurfaceDisabled
            val DisabledText: Color = ColorSemantic.Text.TextDisabled
            val PressedText: Color = ColorSemantic.Text.TextPrimary
        }
        object Text {
            val DefaultBg: Color = ColorSemantic.TransParent.TransParent
            val DefaultText: Color = ColorSemantic.Interactive.InteractivePrimary
            val PressedBg: Color = ColorSemantic.Surface.SurfacePrimary
            val DisabledBg: Color = ColorSemantic.TransParent.TransParent
            val DisabledText: Color = ColorSemantic.Text.TextDisabled
            val PressedText: Color = ColorSemantic.Interactive.InteractivePrimary
        }
    }

    object Toast {
        object Default {
            val Background: Color = ColorSemantic.Surface.OverLaySurface
            val Text: Color = ColorSemantic.Text.OnOverlay
        }
        object Button {
            val Bg: Color = ColorSemantic.Overlay.OverlayContainer
            val Text: Color = ColorSemantic.Overlay.OverlayTextPrimary
        }
    }

    object Dialog {
        object Dialog {
            val DimBg: Color = ColorSemantic.Overlay.OverlayScrim
            val ContainerBg: Color = ColorSemantic.Surface.SurfaceContainer
            val TitleText: Color = ColorSemantic.Text.TextPrimary
            val DescriptionText: Color = ColorSemantic.Text.TextSecondary
        }
    }

    object Cta {
        object Dark {
            val Bg: Color = ColorSemantic.Interactive.InteractiveStrong
            val Text: Color = ColorSemantic.Text.TextOnDark
            val PressedBg: Color = ColorSemantic.Interactive.InteractiveStrongPressed
            val DisabledBg: Color = ColorSemantic.Interactive.InteractiveDisabled
            val DisabledText: Color = ColorSemantic.Text.TextDisabled
        }
        object Primary {
            val Bg: Color = ColorSemantic.Interactive.InteractivePrimary
            val Text: Color = ColorSemantic.Primary.OnPrimary
            val PressedBg: Color = ColorSemantic.Interactive.InteractivePrimaryPressed
            val DisabledBg: Color = ColorSemantic.Interactive.InteractiveDisabled
            val DisabledText: Color = ColorSemantic.Text.TextDisabled
        }
        object White {
            val Bg: Color = ColorSemantic.Surface.SurfaceContainer
            val Text: Color = ColorSemantic.Text.TextPrimary
            val PressedBg: Color = ColorSemantic.Surface.SurfaceContainerPressed
            val DisabledBg: Color = ColorSemantic.Surface.SurfaceContainerDisabled
            val DisabledText: Color = ColorSemantic.Text.TextDisabled
        }
        object TransparentText {
            val Bg: Color = ColorSemantic.TransParent.TransParent
            val Text: Color = ColorSemantic.Interactive.InteractivePrimary
            val PressedBg: Color = ColorSemantic.Surface.SurfacePrimarySubtle
            val DisabledBg: Color = ColorSemantic.TransParent.TransParent
            val DisabledText: Color = ColorSemantic.Text.TextDisabled
        }
    }

    object TextField {
        object Default {
            val Bg: Color = ColorSemantic.Surface.SurfaceField
            val Border: Color = ColorSemantic.Border.BorderDefault
            val Placeholder: Color = ColorSemantic.Text.TextTertiary
            val Icon: Color = ColorSemantic.Icon.IconTertiary
        }
        object Disabled {
            val Bg: Color = ColorSemantic.Surface.SurfaceField
            val Border: Color = ColorSemantic.Border.BorderDisabled
            val Placeholder: Color = ColorSemantic.Text.TextDisabled
            val Icon: Color = ColorSemantic.Icon.IconDisabled
        }
        object Focused {
            val Bg: Color = ColorSemantic.Surface.SurfaceField
            val Border: Color = ColorSemantic.Border.BorderFocus
            val Placeholder: Color = ColorSemantic.Text.TextTertiary
            val Icon: Color = ColorSemantic.Icon.IconTertiary
        }
        object Filled {
            val Bg: Color = ColorSemantic.Surface.SurfaceField
            val Border: Color = ColorSemantic.Border.BorderDefault
            val Placeholder: Color = ColorSemantic.Text.TextTertiary
            val Icon: Color = ColorSemantic.Icon.IconTertiary
        }
        object Error {
            val Bg: Color = ColorSemantic.Surface.SurfaceField
            val Border: Color = ColorSemantic.Border.BorderError
            val Placeholder: Color = ColorSemantic.Text.TextPrimary
            val Icon: Color = ColorSemantic.Icon.IconTertiary
        }
    }

    object TextFieldDisplay {
        object Default {
            val Title: Color = ColorSemantic.Text.TextPrimary
            val Placeholder: Color = ColorSemantic.Text.TextTertiary
        }
        object Focus {
            val Title: Color = ColorSemantic.Text.TextPrimary
            val Text: Color = ColorSemantic.Interactive.InteractivePrimary
        }
        object Error {
            val Title: Color = ColorSemantic.Text.TextPrimary
            val Text: Color = ColorSemantic.Text.TextTertiary
        }
    }

    object Switch {
        object Off {
            val Bg: Color = ColorSemantic.Surface.SurfaceInactive
            val Thumb: Color = ColorSemantic.Surface.SurfaceContainer
        }
        object On {
            val Bg: Color = ColorSemantic.Interactive.InteractivePrimary
            val Thumb: Color = ColorSemantic.Icon.IconOnPrimary
        }
    }

    object Navibar {
        object Container {
            val Background: Color = ColorSemantic.Surface.SurfaceContainer
            val Divider: Color = ColorSemantic.Border.BorderSubtle
        }
        object UnSelected {
            val IconUnSelected: Color = ColorSemantic.Icon.IconTertiary
            val LabelUnSelected: Color = ColorSemantic.Text.TextTertiary
        }
        object Selected {
            val IconSelected: Color = ColorSemantic.Interactive.InteractivePrimary
            val LabelSelected: Color = ColorSemantic.Interactive.InteractivePrimary
        }
    }

    object List {
        object Setting {
            val Text: Color = ColorSemantic.Text.TextPrimary
            val SubText: Color = ColorSemantic.Text.TextSecondary
            val ValueText: Color = ColorSemantic.Text.TextSecondary
            val Background: Color = ColorSemantic.Surface.SurfaceContainer
            val Icon: Color = ColorSemantic.Icon.IconSecondary
            val LeadingIcon: Color = ColorSemantic.Icon.IconPrimary
        }
    }

    object Header {
        object Header {
            val Text: Color = ColorSemantic.Text.TextPrimary
            val Background: Color = ColorSemantic.Surface.SurfaceContainer
            val IconButton: Color = ColorSemantic.Icon.IconPrimary
        }
    }

    object BottomSheet {
        object Selected {
            object Container {
                val Scrim: Color = ColorSemantic.Overlay.OverlayScrim
                val Background: Color = ColorSemantic.Surface.SurfaceContainer
                val Handle: Color = ColorSemantic.Border.BorderDefault
                val Title: Color = ColorSemantic.Text.TextPrimary
                val CloseIcon: Color = ColorSemantic.Icon.IconPrimary
            }
            object ListItem {
                val LeadingIcon: Color = ColorSemantic.Interactive.InteractivePrimary
                val FollowingIcon: Color = ColorSemantic.Text.TextSecondary
                val Label: Color = ColorSemantic.Text.TextPrimary
                val LabelRequired: Color = ColorSemantic.Interactive.InteractivePrimary
                val LabelOptional: Color = ColorSemantic.Text.TextTertiary
            }
        }
        object UnSelected {
            object Container {
                val Scrim: Color = ColorSemantic.Overlay.OverlayScrim
                val Background: Color = ColorSemantic.Surface.SurfaceContainer
                val Handle: Color = ColorSemantic.Border.BorderDefault
                val Title: Color = ColorSemantic.Text.TextPrimary
                val CloseIcon: Color = ColorSemantic.Icon.IconPrimary
            }
            object ListItem {
                val LeadingIcon: Color = ColorSemantic.Icon.IconSecondary
                val FollowingIcon: Color = ColorSemantic.Icon.IconSecondary
                val Label: Color = ColorSemantic.Text.TextSecondary
                val LabelRequired: Color = ColorSemantic.Interactive.InteractivePrimary
                val LabelOptional: Color = ColorSemantic.Text.TextTertiary
            }
        }
    }

}



