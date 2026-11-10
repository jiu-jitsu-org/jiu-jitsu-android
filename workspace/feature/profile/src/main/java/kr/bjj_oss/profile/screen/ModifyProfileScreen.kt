package kr.bjj_oss.profile.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Retained legacy destination uses the same profile presentation and edit behavior. */
@Composable
fun ModifyProfileScreen(
    modifier: Modifier,
    padding: PaddingValues,
    onAcademyClick: (String) -> Unit = {},
    onMyStyleClick: (String) -> Unit = {},
    onBackClick: () -> Unit,
    onNicknameClick: (String) -> Unit = {},
    onCompetitionClick: () -> Unit = {},
    onEditCompetition: (Int, String?) -> Unit = { _, _ -> },
    onCommunityClick: () -> Unit = {},
    onHeaderAppearanceChanged: (Boolean) -> Unit = {},
) {
    ProfileScreen(
        modifier,
        padding,
        onAcademyClick = onAcademyClick,
        onMyStyleClick = onMyStyleClick,
        onNicknameClick = onNicknameClick,
        onCompetitionClick = onCompetitionClick,
        onEditCompetition = onEditCompetition,
        onCommunityClick = onCommunityClick,
        onHeaderAppearanceChanged = onHeaderAppearanceChanged,
        onBackClick = onBackClick
    )
}
