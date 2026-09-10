package kr.bjj_oss.ui.theme

import androidx.compose.ui.graphics.Color

object ColorComponents {

    val BackgroundDefault: Color = ColorSemantic.Surface.BackgroundDefault

    // The JSON also exposes the same background value as the group root.
    val Background: Color = BackgroundDefault

    object Button {
        object Filled {
            val DefaultBg: Color = ColorSemantic.Interactive.InteractivePrimary
            val DefaultText: Color = ColorSemantic.Text.TextOnPrimary
            val PressedBg: Color = ColorSemantic.Primary.PrimaryPressed
            val DisabledBg: Color = ColorSemantic.Interactive.InteractiveDisabled
            val DisabledText: Color = ColorSemantic.Text.TextDisabled
            val PressedText: Color = ColorSemantic.Text.TextOnPrimary
        }
        object Tint {
            val DefaultBg: Color = ColorSemantic.Surface.SurfacePrimarySubtle
            val DefaultText: Color = ColorSemantic.Interactive.InteractivePrimary
            val PressedBg: Color = ColorSemantic.Surface.SurfacePrimarySubtlePressed
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
            val DefaultBg: Color = ColorSemantic.Transparent.Transparent
            val DefaultText: Color = ColorSemantic.Interactive.InteractivePrimary
            val PressedBg: Color = ColorSemantic.Surface.SurfacePrimarySubtle
            val DisabledBg: Color = ColorSemantic.Transparent.Transparent
            val DisabledText: Color = ColorSemantic.Text.TextDisabled
            val PressedText: Color = ColorSemantic.Interactive.InteractivePrimary
        }
        object Inverted {
            val DefaultBg: Color = ColorSemantic.Transparent.Transparent
            val DefaultText: Color = ColorSemantic.Primary.OnPrimary
            val PressedBg: Color = ColorSemantic.Surface.OnDarkPressedBg
            val PressedText: Color = ColorSemantic.Primary.OnPrimary
            val DisabledBg: Color = ColorSemantic.Transparent.Transparent
            val DisabledText: Color = ColorSemantic.Icon.IconDisabledOnDark
        }
        object InvertedSubtle {
            val DefaultBg: Color = ColorSemantic.Transparent.Transparent
            val DefaultText: Color = ColorSemantic.Icon.IconOnDarkSubtle
            val PressedBg: Color = ColorSemantic.Surface.OnDarkPressedBg
            val PressedText: Color = ColorSemantic.Primary.OnPrimary
            val DisabledBg: Color = ColorSemantic.Transparent.Transparent
            val DisabledText: Color = ColorSemantic.Icon.IconDisabledOnDark
        }
        object Ghost {
            val DefaultText: Color = ColorSemantic.Text.TextPrimary
            val PressedText: Color = ColorSemantic.Text.TextPrimary
            val DisabledText: Color = ColorSemantic.Text.TextDisabled
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
            val Bg: Color = ColorSemantic.Transparent.Transparent
            val Text: Color = ColorSemantic.Interactive.InteractivePrimary
            val PressedBg: Color = ColorSemantic.Surface.SurfacePrimarySubtle
            val DisabledBg: Color = ColorSemantic.Transparent.Transparent
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
            val Placeholder: Color = ColorSemantic.Text.TextPrimary
            val Icon: Color = ColorSemantic.Text.TextTertiary
        }
        object Error {
            val Bg: Color = ColorSemantic.Surface.SurfaceField
            val Border: Color = ColorSemantic.Border.BorderError
            val Placeholder: Color = ColorSemantic.Text.TextPrimary
            val Icon: Color = ColorSemantic.Text.TextTertiary
        }
    }

    object TextFieldMultiline {
        object Default {
            val Bg: Color = ColorSemantic.Surface.SurfaceField
            val Text: Color = ColorSemantic.Text.TextTertiary
        }
        object Focused {
            val Bg: Color = ColorSemantic.Surface.SurfaceField
            val Border: Color = ColorSemantic.Border.BorderFocus
            val Text: Color = ColorSemantic.Text.TextPrimary
        }
        object Filled {
            val Bg: Color = ColorSemantic.Surface.SurfaceField
            val Text: Color = ColorSemantic.Text.TextPrimary
        }
        object Disabled {
            val Bg: Color = ColorSemantic.Surface.SurfaceField
            val Text: Color = ColorSemantic.Text.TextDisabled
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

    object Navbar {
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
            val LabelSelected: Color = ColorSemantic.Interactive.InteractivePrimaryPressed
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
            val Background: Color = ColorSemantic.Surface.BackgroundDefault
            val IconButton: Color = ColorSemantic.Icon.IconPrimary
            val DestructiveText: Color = ColorSemantic.Destructive.Destructive
            val BellOff: Color = ColorSemantic.Icon.IconTertiary
            val BellOn: Color = ColorSemantic.Interactive.InteractivePrimary
        }
    }

    object SectionHeader {
        val Title: Color = ColorSemantic.Text.TextPrimary
        val SubTitle: Color = ColorSemantic.Text.TextSecondary
        val Label: Color = ColorSemantic.Text.TextSecondary
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

    object Segment {
        object Container {
            val Bg: Color = ColorSemantic.Surface.SurfaceTertiary
        }
        object Selected {
            val Bg: Color = ColorSemantic.Surface.SurfaceContainer
            val TitleText: Color = ColorSemantic.Text.TextPrimary
            val SubText: Color = ColorSemantic.Text.TextSecondary
        }
        object UnSelected {
            val Bg: Color = ColorSemantic.Transparent.Transparent
            val TitleText: Color = ColorSemantic.Text.TextSecondary
            val SubText: Color = ColorSemantic.Text.TextTertiary
        }
    }

    object SkillCard {
        object Container {
            val Bg: Color = ColorSemantic.Surface.SurfaceTertiary
        }
        object Default {
            val Bg: Color = ColorSemantic.Surface.SurfaceContainer
            val LabelText: Color = ColorSemantic.Text.TextTertiary
            val TitleTextFilled: Color = ColorSemantic.Text.TextPrimary
            val TitleTextEmpty: Color = ColorSemantic.Text.TextPrimary
            val TitleTextFilled2: Color = ColorSemantic.Text.TextPrimary
            val IconBgEmpty: Color = ColorSemantic.Surface.SurfaceDisabled
        }
        object Editable {
            val Bg: Color = ColorSemantic.Surface.SurfaceContainer
            val Border: Color = ColorSemantic.Border.BorderDefault
            val LabelText: Color = ColorSemantic.Text.TextTertiary
            val LabelText2: Color = ColorSemantic.Text.TextTertiary
            val TitleTextFilled: Color = ColorSemantic.Text.TextPrimary
            val TitleTextEmpty: Color = ColorSemantic.Text.TextPrimary
            val IconBgEmpty: Color = ColorSemantic.Surface.SurfaceDisabled

            // Compatibility alias for the previous misspelled Android token name.
            val Bolder: Color = Border
        }
    }

    object BeltCard {
        object Default {
            val Bg: Color = ColorSemantic.Surface.SurfaceContainer
            val Text: Color = ColorSemantic.Text.TextPrimary
        }
        object Filled {
            val Bg: Color = ColorSemantic.Surface.SurfaceContainer
            val Divider: Color = ColorSemantic.Border.BorderDefault
            val LabelText: Color = ColorSemantic.Text.TextTertiary
            val ContentText: Color = ColorSemantic.Text.TextPrimary
        }
    }

    object CompetitionCard {
        val TitleText: Color = ColorSemantic.Text.TextPrimary
        val CardBg: Color = ColorSemantic.Surface.SurfaceContainer
        val CardIcon: Color = ColorSemantic.Icon.IconSecondary
        val CardTextPrimary: Color = ColorSemantic.Text.TextPrimary
        val CardTextSecondary: Color = ColorSemantic.Text.TextTertiary
    }

    object Picker {
        val ItemSelectedBg: Color = ColorSemantic.Surface.SurfaceSecondary
        val ItemSelectedText: Color = ColorSemantic.Text.TextPrimary
        val ItemUnSelectedText: Color = ColorSemantic.Text.TextTertiary
        val Unit: Color = ColorSemantic.Text.TextPrimary
    }

    object MyProfileHeader {
        val ProfileImagePlaceholder: Color = ColorSemantic.Surface.SurfaceContainer
        val NicknameText: Color = ColorSemantic.Primary.OnPrimary
        val ProfileImageDefaultIcon: Color = ColorSemantic.Icon.IconSubtle

        object Bg {
            val Default: Color = Blue300
            val White: Color = CoolGray200
            val Blue: Color = Blue500
            val Purple: Color = BeltPurple
            val Brown: Color = BeltBrown
            val Black: Color = BeltBlack
        }
    }

    object FeedCard {
        val EndText: Color = ColorSemantic.Text.TextTertiary

        object Header {
            val AvatarBg: Color = ColorSemantic.Surface.SurfaceSecondary
            val UsernameText: Color = ColorSemantic.Text.TextPrimary
            val DateText: Color = ColorSemantic.Text.TextTertiary
            val MoreIcon: Color = ColorSemantic.Icon.IconSubtle
        }

        object Body {
            val TitleText: Color = ColorSemantic.Text.TextPrimary
            val BodyText: Color = ColorSemantic.Text.TextSecondary
            val MoreText: Color = ColorSemantic.Text.TextTertiary
        }

        object ImageBadge {
            val Bg: Color = ColorSemantic.Overlay.OverlayScrimHeavy
            val Text: Color = ColorSemantic.Text.OnOverlay
        }

        object Tag {
            val Text: Color = ColorSemantic.Text.TextTertiary
        }
    }

    object Poll {
        object StickyBar {
            val Bg: Color = ColorSemantic.Surface.SurfacePrimarySubtle
            val Text: Color = ColorSemantic.Interactive.InteractivePrimary
            val Icon: Color = ColorSemantic.Interactive.InteractivePrimary
        }

        object Option {
            object A {
                object Default {
                    val Bg: Color = ColorSemantic.Surface.SurfacePollA
                    val Text: Color = ColorSemantic.Text.TextSecondary
                }

                object Selected {
                    val Bg: Color = ColorSemantic.Surface.SurfacePollAStrong
                    val BgTrack: Color = ColorSemantic.Surface.SurfacePollATrack
                    val Text: Color = ColorSemantic.Text.TextOnPrimary
                    val PercentText: Color = ColorSemantic.Interactive.InteractivePollA
                }

                object Result {
                    val BgFill: Color = ColorSemantic.Surface.SurfacePollATrack
                    val BgTrack: Color = ColorSemantic.Surface.SurfacePollA
                    val Text: Color = ColorSemantic.Text.TextSecondary
                    val PercentText: Color = ColorSemantic.Text.TextSecondary
                }
            }

            object B {
                object Default {
                    val Bg: Color = ColorSemantic.Surface.SurfacePollB
                    val Text: Color = ColorSemantic.Text.TextSecondary
                }

                object Selected {
                    val Bg: Color = ColorSemantic.Surface.SurfacePollBStrong
                    val BgTrack: Color = ColorSemantic.Surface.SurfacePollBTrack
                    val Text: Color = ColorSemantic.Text.TextOnPrimary
                    val PercentText: Color = ColorSemantic.Interactive.InteractivePollB
                }

                object Result {
                    val BgFill: Color = ColorSemantic.Surface.SurfacePollBTrack
                    val BgTrack: Color = ColorSemantic.Surface.SurfacePollB
                    val Text: Color = ColorSemantic.Text.TextSecondary
                    val PercentText: Color = ColorSemantic.Text.TextSecondary
                    val Bg: Color = TrueWhite
                }
            }
        }
    }

    object ReactionBar {
        object Default {
            val Icon: Color = ColorSemantic.Icon.IconSubtle
            val CountText: Color = ColorSemantic.Text.TextTertiary
        }

        object Disabled {
            val Icon: Color = ColorSemantic.Icon.IconTertiary
            val CountText: Color = ColorSemantic.Text.TextTertiary
        }

        object Active {
            val LikeIcon: Color = ColorSemantic.Error
            val CommentIcon: Color = ColorSemantic.Icon.IconSecondary
            val BookmarkIcon: Color = ColorSemantic.Interactive.InteractivePrimary
            val CountText: Color = ColorSemantic.Text.TextSecondary
        }

        object Pressed {
            val Bg: Color = ColorSemantic.Surface.SurfacePrimarySubtle
            val Icon: Color = ColorSemantic.Interactive.InteractivePrimary
            val CountText: Color = ColorSemantic.Interactive.InteractivePrimary
        }

        object Detail {
            object Default {
                val Bg: Color = ColorSemantic.Surface.SurfaceFieldSubtle
                val CountText: Color = ColorSemantic.Text.TextSecondary
                val Icon: Color = ColorSemantic.Icon.IconSecondary
            }

            object Disabled {
                val Icon: Color = ColorSemantic.Icon.IconTertiary
                val CountText: Color = ColorSemantic.Text.TextTertiary
            }

            object Active {
                val LikeIcon: Color = ColorSemantic.Error
                val BookmarkIcon: Color = ColorSemantic.Interactive.InteractivePrimary
                val CommentIcon: Color = ColorSemantic.Icon.IconSecondary
                val CountText: Color = ColorSemantic.Text.TextSecondary
            }

            object Pressed {
                val CountText: Color = ColorSemantic.Interactive.InteractivePrimary
                val Bg: Color = ColorSemantic.Surface.SurfacePrimarySubtle
                val Icon: Color = ColorSemantic.Interactive.InteractivePrimary
            }
        }
    }

    object EmptyState {
        object Default {
            val TitleText: Color = ColorSemantic.Text.TextPrimary
            val DescriptionText: Color = ColorSemantic.Text.TextSecondary
        }
    }

    object ErrorState {
        object Default {
            val TitleText: Color = ColorSemantic.Text.TextPrimary
            val DescriptionText: Color = ColorSemantic.Text.TextSecondary
        }
    }

    object TabBar {
        object Selected {
            val Text: Color = ColorSemantic.Interactive.InteractivePrimary
            val Underline: Color = ColorSemantic.Interactive.InteractivePrimary
        }

        object Unselected {
            val Text: Color = ColorSemantic.Text.TextPrimary
        }
    }

    object CommentInputBar {
        val ContainerBg: Color = ColorSemantic.Surface.SurfaceContainer
        val Bg: Color = ColorSemantic.Surface.SurfaceField
        val Text: Color = ColorSemantic.Text.TextPrimary
        val Placeholder: Color = ColorSemantic.Text.TextTertiary
        val TextDisabled: Color = ColorSemantic.Text.TextDisabled
        val SendIconDisabled: Color = ColorSemantic.Icon.IconDisabled
        val SendIconActive: Color = ColorSemantic.Interactive.InteractivePrimary
    }

    object CommentSortSelect {
        object Default {
            val Text: Color = ColorSemantic.Text.TextPrimary
        }

        object Pressed {
            val Bg: Color = ColorSemantic.Surface.SurfaceSecondary
            val Text: Color = ColorSemantic.Text.TextPrimary
            val Text2: Color = ColorSemantic.Surface.SurfaceField
            val TextDisabled: Color = ColorSemantic.Text.TextDisabled
        }

        object Disabled {
            val Text: Color = ColorSemantic.Text.TextDisabled
        }
    }

    object TagChip {
        object Default {
            val Bg: Color = ColorSemantic.Transparent.Transparent
            val Text: Color = ColorSemantic.Text.TextPrimary
        }

        object Selected {
            val Bg: Color = ColorSemantic.Surface.SurfaceField
            val Bg2: Color = ColorSemantic.Transparent.Transparent
            val Text: Color = ColorSemantic.Text.TextPrimary
        }

        object Disabled {
            val Bg: Color = ColorSemantic.Transparent.Transparent
            val Text: Color = ColorSemantic.Text.TextDisabled
        }
    }

    object Divider {
        val Bg: Color = ColorSemantic.Surface.SurfaceBackground
    }

    object CommentReplies {
        val Text: Color = ColorSemantic.Text.TextSecondary
    }

    object CommentTombstone {
        val Icon: Color = ColorSemantic.Icon.IconSecondary
        val Text: Color = ColorSemantic.Text.TextSecondary
    }

    object CommentAuthorBadge {
        val Bg: Color = ColorSemantic.Surface.SurfacePrimarySubtle
        val Text: Color = ColorSemantic.Interactive.InteractivePrimary
    }

    object CommentThreadLine {
        val Stroke: Color = ColorSemantic.Border.BorderSubtle
    }

    object ConfirmDialog {
        object DestructiveButton {
            val Bg: Color = ColorSemantic.Surface.SurfaceDestructive
            val Text: Color = ColorSemantic.Text.TextOnPrimary
        }
    }

    object Radio {
        object Selected {
            val Border: Color = ColorSemantic.Interactive.InteractivePrimary
            val Dot: Color = ColorSemantic.Interactive.InteractivePrimary
            val Bg: Color = ColorSemantic.Surface.SurfaceContainer
        }

        object Unselected {
            val Border: Color = ColorSemantic.Border.BorderDefault
            val Bg: Color = ColorSemantic.Surface.SurfaceContainer
        }
    }

    object ImageLoadError {
        val Bg: Color = ColorSemantic.Surface.SurfaceField
        val Text: Color = ColorSemantic.Text.TextSecondary
    }

}
