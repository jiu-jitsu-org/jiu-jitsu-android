package com.kyu.jiu_jitsu.ui.theme

import androidx.compose.ui.graphics.Color
import com.kyu.jiu_jitsu.model.BELT_RANK
import com.kyu.jiu_jitsu.ui.R

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Black = Color(0xFF0D0D0D)
val TrueBlack = Color(0xFF000000)
val White = Color(0xFFFAFAFA)
val TrueWhite = Color(0xFFFFFFFF)

val Blue50 = Color(0xFFE6F5FF)
val Blue75 = Color(0xFFC2E5FF)
val Blue100 = Color(0xFFB0DDFF)
val Blue200 = Color(0xFF8ACCFF)
val Blue300 = Color(0xFF5CB8FF)
val Blue400 = Color(0xFF2DA3FC)
val Blue500 = Color(0xFF0090FF)
val Blue600 = Color(0xFF007BE0)
val Blue700 = Color(0xFF0065B8)
val Blue800 = Color(0xFF004F8F)
val Blue900 = Color(0xFF033969)
val Blue1000 = Color(0xFF032645)
val BlueBalance50 = Color(0xFFEBF3FE)
val BlueBalance100 = Color(0xFFB2D0F8)
val BlueBalance500 = Color(0xFF3967EB)

val CoolGray10 = Color(0xFFF5F5F5)
val CoolGray25 = Color(0xFFEDEFF0)
val CoolGray50 = Color(0xFFE6E7E8)
val CoolGray75 = Color(0xFFD7D8D9)
val CoolGray100 = Color(0xFFCECFD1)
val CoolGray200 = Color(0xFFB7B9BD)
val CoolGray300 = Color(0xFF9C9EA6)
val CoolGray400 = Color(0xFF83868F)
val CoolGray500 = Color(0xFF70737C)
val CoolGray600 = Color(0xFF63666E)
val CoolGray700 = Color(0xFF4D4E54)
val CoolGray800 = Color(0xFF37383D)
val CoolGray900 = Color(0xFF292A2E)

val Red500 = Color(0xFFFF1D0D)
val RedBalance50 = Color(0xFFFDEFEE)
val RedBalance100 = Color(0xFFFFC1BD)
val RedBalance500 = Color(0xFFD53E4C)
val RedOpacity10 = Color(0x1AFF1D0D)

val BlackOpacity40 = Color(0x66000000)
val BlackOpacity80 = Color(0xCC000000)

val WhiteOpacity20 = Color(0x33FFFFFF)
val WhiteOpacity40 = Color(0x66FFFFFF)
val WhiteOpacity60 = Color(0x99FFFFFF)
val WhiteOpacity80 = Color(0xCCFFFFFF)

val BlueOpacity10 = Color(0x1A009DFF)
val BlueOpacity40 = Color(0x66009DFF)

val KakaoBg = Color(0xFFFEE500)
val AppleBg = Color(0xFF050708)

val BeltPurple = Color(0xFFCD57FF)
val BeltBrown = Color(0xFFA66040)
val BeltBlack = Color(0xFF2D2D2D)

val Color1F1F1F = Color(0xFF1F1F1F)

fun BELT_RANK.color(): Color = when (this) {
    is BELT_RANK.BLACK -> ColorComponents.MyProfileHeader.Bg.Black
    is BELT_RANK.BLUE -> ColorComponents.MyProfileHeader.Bg.Blue
    is BELT_RANK.BROWN -> ColorComponents.MyProfileHeader.Bg.Brown
    is BELT_RANK.PURPLE -> ColorComponents.MyProfileHeader.Bg.Purple
    is BELT_RANK.WHITE -> ColorComponents.MyProfileHeader.Bg.White
}

fun BELT_RANK.drawableRes(): Int = when (this) {
    is BELT_RANK.BLACK -> R.drawable.ic_black_belt
    is BELT_RANK.BLUE -> R.drawable.ic_blue_belt
    is BELT_RANK.BROWN -> R.drawable.ic_brown_belt
    is BELT_RANK.PURPLE -> R.drawable.ic_purple_belt
    is BELT_RANK.WHITE -> R.drawable.ic_white_belt
}
