package kr.bjj_oss.profile.model

/** Tab presentation for the restored best/favorite style editor. */
sealed class StyleTabItem(open val type: String, open val selectedItem: String) {
    data class Best(override val type: String = "best", override val selectedItem: String = "") : StyleTabItem(type, selectedItem)
    data class Favorite(override val type: String = "favorite", override val selectedItem: String = "") : StyleTabItem(type, selectedItem)
}
