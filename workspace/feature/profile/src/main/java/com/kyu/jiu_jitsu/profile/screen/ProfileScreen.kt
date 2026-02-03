package com.kyu.jiu_jitsu.profile.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.model.CommunityProfileInfo
import com.kyu.jiu_jitsu.data.model.isShowModify
import com.kyu.jiu_jitsu.data.model.singleton.ProfileSingleton
import com.kyu.jiu_jitsu.profile.ProfileViewModel
import com.kyu.jiu_jitsu.profile.components.BeltRankAndWeightBottomSheet
import com.kyu.jiu_jitsu.profile.components.BeltRankAndWeightLayout
import com.kyu.jiu_jitsu.profile.components.CompetitionLayout
import com.kyu.jiu_jitsu.profile.components.MySkillLayout
import com.kyu.jiu_jitsu.ui.R
import com.kyu.jiu_jitsu.ui.components.button.PressableTextButton
import com.kyu.jiu_jitsu.ui.components.button.TintButton
import com.kyu.jiu_jitsu.ui.routes.SkillStyleScreenType
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.CoolGray25
import com.kyu.jiu_jitsu.ui.theme.CoolGray75
import com.kyu.jiu_jitsu.ui.theme.White
import com.kyu.jiu_jitsu.ui.theme.color

/**
 * 프로필 화면
 * @param modifier Modifier
 * @param padding PaddingValues
 * @param onModifyClick 정보 수정 클릭
 * @param savedStateHandle 뒤로가기 정보 연동
 * @param onAcademyClick 도장 정보 클릭
 * @param onMyStyleClick 스타일(포지션, 스킬, 서브미션) 클릭
 * @param onCompetitionClick 대회 정보 추가 클릭
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    onModifyClick: () -> Unit = {},
    savedStateHandle: SavedStateHandle,
    onAcademyClick: (name: String) -> Unit = {},
    onMyStyleClick: (screenName: String) -> Unit = {},
    onCompetitionClick: () -> Unit = {},
) {
    val viewModel = hiltViewModel<ProfileViewModel>()

    /** 프로필 UI BG Color */
    var profileBgColor by remember { mutableStateOf(ProfileSingleton.getBeltRank?.color() ?: ColorComponents.MyProfileHeader.Bg.Default) }
    /** 프로필 User Academy Name*/
    var profileAcademyName by remember { mutableStateOf<String?>(ProfileSingleton.getAcademyName) }
    /** 프로필 Uer Nickname */
    var profileUserNickName by remember { mutableStateOf<String?>(ProfileSingleton.getNickName) }
    /** 프로필 Uer ProfileImg */
    var profileUserProfileImg by remember { mutableStateOf<String?>(ProfileSingleton.getProfileImage) }
    /** 프로필 나의 주짓수 (특기, 포지션, 기술) */
    var profileMyStyle by remember { mutableStateOf<CommunityProfileInfo?>(null) }

    /** 수정버튼 노출 여부 */
    var showModifyBtn by remember { mutableStateOf(ProfileSingleton.isShowModifyBtn) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val isFirstItemFullyVisible by remember {
//        derivedStateOf {
//            listState.layoutInfo.visibleItemsInfo.any { it.index == 0 }
//        }
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    /** Init UI */
    val initProfileUi: (data: CommunityProfileInfo?) -> Unit = { data ->
        data?.let {
            /** NickName */
            if (profileUserNickName != it.nickname) {
                val newName = it.nickname.ifEmpty { viewModel.localNickname }
                profileUserNickName = newName
            }
            /** Profile Image */
            if (profileUserProfileImg != it.profileImageUrl) {
                val newUrl = it.profileImageUrl?.ifEmpty { null }
                profileUserProfileImg = newUrl
            }
            /** AcademyName */
            if (profileAcademyName != it.academyName) {
                val newName = it.academyName.ifEmpty { null }
                profileAcademyName = newName
            }
            /** Profile Bg Color */
            profileBgColor = it.beltRank?.color() ?: ColorComponents.MyProfileHeader.Bg.Default
            if(profileMyStyle != it) {
                profileMyStyle = it
            }
            /** Set Modify Btn */
            showModifyBtn = it.isShowModify()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initProfileData()
    }

    LaunchedEffect(savedStateHandle) {
        savedStateHandle
            .getStateFlow<String?>("isCompetitionUpdated", null)
            .collect { isCompetitionUpdated ->
                isCompetitionUpdated?.let {
                    if (it.isNotEmpty()) {
                        initProfileUi(ProfileSingleton.profileInfo)
                    }
                }
            }
    }

    LaunchedEffect(viewModel.profileUiState) {
        val uiState = viewModel.profileUiState
        when(uiState) {
            is UiState.Success -> {
                initProfileUi(uiState.result)
            }
            is UiState.Error -> {
                profileUserNickName = viewModel.localNickname
            }
            is UiState.Loading -> {

            }
            else -> {}
        }
    }

    Surface(
        modifier = modifier,
        color = profileBgColor,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = White),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = White),
                state = listState,
            ) {
                /** 상단 TopBar */
                item {
                    Row(
                        modifier = Modifier
                            .height(55.dp)
                            .fillMaxWidth()
                            .background(color = profileBgColor)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        if (showModifyBtn) {
                            PressableTextButton(
                                text = stringResource(R.string.common_modify),
                                onClick = onModifyClick,
                                enableTextColor = White,
                                pressedTextColor = White,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(27.dp).fillMaxWidth().background(color = profileBgColor))
                }
                /** 유저 프로필 + 닉네임 */
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().background(color = profileBgColor),
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
                        if ((profileAcademyName?:"").isEmpty()) {
                            TintButton(
                                text = stringResource(com.kyu.jiu_jitsu.profile.R.string.profile_input_academy),
                                onClick = { onAcademyClick(profileAcademyName ?: "") },
                            )
                        } else {
//                        Text(
//                            text = profileAcademyName ?: "",
//                            style = MaterialTheme.typography.titleMedium,
//                            color = CoolGray75,
//                        )
                            TintButton(
                                text = profileAcademyName ?: "",
                                textStyle = MaterialTheme.typography.titleMedium,
                                enableTextColor = CoolGray75,
                                pressedTextColor = CoolGray75,
                                onClick = { onAcademyClick(profileAcademyName ?: "") },
                            )
                        }

                        Spacer(modifier = Modifier.height(36.dp).fillMaxWidth().background(color = profileBgColor))
                    }
                }
                /** 벨트, 체급 */
                item {
                    ConstraintLayout(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .background(
                                color = White,
                            )
                    ) {
                        val (infoRow, bg) = createRefs()

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(
                                    color = profileBgColor,
                                )
                                .constrainAs(bg) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }
                        )

                        Surface(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .constrainAs(infoRow) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    bottom.linkTo(parent.bottom)
                                },
                            shadowElevation = 6.dp,
                            shape = RoundedCornerShape(16.dp),
                            color = ColorComponents.List.Setting.Background,
                        ) {
                            BeltRankAndWeightLayout(
                                beltRank = profileMyStyle?.beltRank,
                                beltStripe = profileMyStyle?.beltStripe,
                                weightKg = profileMyStyle?.weightKg,
                                isWeightHidden = profileMyStyle?.isWeightHidden ?: false,
                                onSaveBeltWeightClick = { openBottomSheet = true },
                                onSaveWeightHiddenClick = { isWeightHidden ->
                                    viewModel.changeBeltAndWeight(
                                        isWeightHidden = isWeightHidden
                                    )
                                }
                            )
                        }

                    }

                }
                /** 나의 주짓수 (특기, 포지션, 기술) */
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    MySkillLayout(
                        info = profileMyStyle,
                        onSaveMyStyleClick = {
                            onMyStyleClick(SkillStyleScreenType.ALL.screenName)
                        }
                    )
                }
                /** 대회 정보 */
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    CompetitionLayout(
                        onAddCompetitionClickEvent = onCompetitionClick
                    )
                    Spacer(modifier = Modifier.height(130.dp))
                }
            }

            /** 상단 흰 상태바 (스크롤 최상단이 아닐 경우에만 노출) */
            AnimatedVisibility(
                visible = !isFirstItemFullyVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                // TODO chan 상단 상태바 높이를 동적으로 구해볼 수 있을까?
                Spacer(
                    modifier = Modifier
                        .height(55.dp)
                        .fillMaxWidth()
                        .background(color = White)
                        .align(Alignment.TopCenter)
                )
            }
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