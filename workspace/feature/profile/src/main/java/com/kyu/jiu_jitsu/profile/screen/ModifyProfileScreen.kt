package com.kyu.jiu_jitsu.profile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.model.BELT_RANK
import com.kyu.jiu_jitsu.data.model.CommunityProfileInfo
import com.kyu.jiu_jitsu.profile.ProfileViewModel
import com.kyu.jiu_jitsu.profile.components.BeltRankAndWeightBottomSheet
import com.kyu.jiu_jitsu.profile.components.BeltRankAndWeightLayout
import com.kyu.jiu_jitsu.profile.components.CompetitionLayout
import com.kyu.jiu_jitsu.profile.components.MySkillLayout
import com.kyu.jiu_jitsu.ui.R
import com.kyu.jiu_jitsu.ui.components.button.PressableTextButton
import com.kyu.jiu_jitsu.ui.components.button.TintButton
import com.kyu.jiu_jitsu.ui.routes.SkillStyleScreenType
import com.kyu.jiu_jitsu.ui.theme.Blue300
import com.kyu.jiu_jitsu.ui.theme.Blue500
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.CoolGray25
import com.kyu.jiu_jitsu.ui.theme.CoolGray75
import com.kyu.jiu_jitsu.ui.theme.White
import kotlin.text.ifEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyProfileScreen(
    modifier: Modifier,
    padding: PaddingValues,
    savedStateHandle: SavedStateHandle,
    onAcademyClick: (name: String) -> Unit = {},
    onMyStyleClick: (screenName: String) -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    val viewModel = hiltViewModel<ProfileViewModel>()

    /** 프로필 User Academy Name*/
    var profileAcademyName by remember { mutableStateOf<String?>(null) }
    /** 프로필 Uer Nickname */
    var profileUserNickName by remember { mutableStateOf<String?>(null) }
    /** 프로필 Uer ProfileImg */
    var profileUserProfileImg by remember { mutableStateOf<String?>(null) }
    /** 프로필 나의 주짓수 (특기, 포지션, 기술) */
    var profileMyStyle by remember { mutableStateOf<CommunityProfileInfo?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.initProfileData()
    }

    LaunchedEffect(savedStateHandle) {
        savedStateHandle
            .getStateFlow<String?>("academyName", null)
            .collect { academyName ->
                academyName?.let {
                    if (it.isNotEmpty()) {
                        viewModel.changeAcademyName(it)
                    }
                }
            }
    }

    LaunchedEffect(viewModel.profileUiState) {
        val uiState = viewModel.profileUiState
        when(uiState) {
            is UiState.Success -> {
                with(uiState.result) {
                    /** NickName */
                    if (profileUserNickName != nickname) {
                        val newName = nickname.ifEmpty { null }
                        profileUserNickName = newName
                    }
                    /** Profile Image */
                    if (profileUserProfileImg != profileImageUrl) {
                        val newUrl = profileImageUrl?.ifEmpty { null }
                        profileUserProfileImg = newUrl
                    }
                    /** AcademyName */
                    if (profileAcademyName != academyName) {
                        val newName = academyName.ifEmpty { null }
                        profileAcademyName = newName
                    }
                    if(profileMyStyle != this) {
                        profileMyStyle = this
                    }
                }

            }
            is UiState.Error -> {

            }
            is UiState.Loading -> {

            }
            else -> {}
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = CoolGray25,
    ) {

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            /** 상단 TopBar */
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .paint(
                                painter = painterResource(R.drawable.ic_blue_left_back_button),
                                contentScale = ContentScale.Crop,
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true),
                                role = Role.Button,
                                onClick = onBackClick
                            )
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(com.kyu.jiu_jitsu.profile.R.string.profile_modify_title),
                        style = MaterialTheme.typography.displayLarge,
                        color = ColorComponents.TextFieldDisplay.Default.Title,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(36.dp))
                }
                Spacer(modifier = Modifier.height(27.dp).fillMaxWidth())
            }
            /** 유저 프로필 + 닉네임 */
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    /** 프로필 이미지 */
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(
                                color = White,
                                shape = RoundedCornerShape(24.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (profileUserProfileImg == null) {
                            Icon(
                                modifier = Modifier.size(64.dp),
                                painter = painterResource(R.drawable.ic_profile_default),
                                contentDescription = "Profile Image",
                                tint = CoolGray25,
                            )
                        } else {
                            Icon(
                                modifier = Modifier.size(64.dp),
                                painter = painterResource(R.drawable.ic_profile_default),
                                contentDescription = "Profile Image",
                                tint = CoolGray25,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    /** 닉네임 */
                    Text(
                        text = profileUserNickName ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = ColorComponents.List.Setting.Background
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    /** 도장 정보 입력 / 도장 이름  */
                    if (profileAcademyName == null) {
                        TintButton(
                            text = stringResource(com.kyu.jiu_jitsu.profile.R.string.profile_input_academy),
                            onClick = { onAcademyClick(profileAcademyName ?: "") },
                        )
                    } else {
                        Text(
                            text = profileAcademyName ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            color = CoolGray75
                        )
                    }

                    Spacer(modifier = Modifier.height(36.dp).fillMaxWidth())
                }
            }
            /** 벨트, 체급 */
            item {
                BeltRankAndWeightLayout(
                    beltRank = profileMyStyle?.beltRank,
                    beltStripe = profileMyStyle?.beltStripe,
                    weightKg = profileMyStyle?.weightKg,
                    isWeightHidden = profileMyStyle?.isWeightHidden ?: false,
                    onSaveBeltWeightClick = { openBottomSheet = true },
                )

            }
            /** 나의 주짓수 (특기, 포지션, 기술) */
//            item {
//                Spacer(modifier = Modifier.height(10.dp))
//                MySkillLayout(
//                    info = profileMyStyle,
//                    onSaveMyStyleClick = {
//                        onMyStyleClick(SkillStyleScreenType.Position.screenName)
//                    }
//                )
//            }
            /** 대회 정보 */
//            item {
//                Spacer(modifier = Modifier.height(10.dp))
//                CompetitionLayout(
//                    onAddCompetitionClickEvent = {},
//                    isModifyMode = true,
//                )
//            }
        }

        /** 벨트/체급 선택 바텀 시트 */
        if (openBottomSheet) {
            ModalBottomSheet(
                containerColor = ColorComponents.BottomSheet.Selected.Container.Background,
                contentColor = ColorComponents.BottomSheet.Selected.Container.Title,
                scrimColor = ColorComponents.BottomSheet.Selected.Container.Scrim,
                sheetState = sheetState,
                onDismissRequest = {
                    openBottomSheet = false
                },
                shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
                dragHandle = {
                    BottomSheetDefaults.DragHandle(
                        width = 48.dp,
                        height = 4.dp,
                        color = ColorComponents.BottomSheet.Selected.Container.Handle
                    )
                },
            ) {
                BeltRankAndWeightBottomSheet { beltRank, beltStripe, gender, weightKg, isWeightHidden ->
                    openBottomSheet = false
                    viewModel.changeBeltAndWeight(beltRank, beltStripe, gender, weightKg, isWeightHidden)
                }
            }
        }
    }
}