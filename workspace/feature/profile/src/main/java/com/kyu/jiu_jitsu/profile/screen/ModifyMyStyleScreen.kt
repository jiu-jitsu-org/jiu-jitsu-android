package com.kyu.jiu_jitsu.profile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.profile.ProfileViewModel
import com.kyu.jiu_jitsu.profile.R
import com.kyu.jiu_jitsu.profile.components.style.CardBackLayout
import com.kyu.jiu_jitsu.profile.components.style.CardFrontLayout
import com.kyu.jiu_jitsu.profile.components.style.CardIndicatorLayout
import com.kyu.jiu_jitsu.profile.components.style.SegmentedPositionTabBar
import com.kyu.jiu_jitsu.profile.model.POSITION_INDICATOR_LIST
import com.kyu.jiu_jitsu.profile.model.POSITION_LIST
import com.kyu.jiu_jitsu.profile.model.SUBMISSION_INDICATOR_LIST
import com.kyu.jiu_jitsu.profile.model.SUBMISSION_LIST
import com.kyu.jiu_jitsu.profile.model.StyleCardIndicator
import com.kyu.jiu_jitsu.profile.model.TECHNIQUE_INDICATOR_LIST
import com.kyu.jiu_jitsu.profile.model.TECHNIQUE_LIST
import com.kyu.jiu_jitsu.ui.components.button.PrimaryButton
import com.kyu.jiu_jitsu.ui.components.button.TintButton
import com.kyu.jiu_jitsu.ui.components.card.DraggableFlipCard
import com.kyu.jiu_jitsu.ui.routes.SkillStyleScreenType
import com.kyu.jiu_jitsu.ui.theme.Blue500
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.TrueWhite
import com.kyu.jiu_jitsu.ui.theme.White
import com.kyu.jiu_jitsu.ui.theme.WhiteOpacity40
import com.kyu.jiu_jitsu.ui.theme.getBgDrawableRes
import com.kyu.jiu_jitsu.ui.theme.getIconDrawableRes

sealed class StyleTabItem(open val type: String, open val selectedItem: String) {
    data class Best(
        override val type: String = "best",
        override val selectedItem: String = "",
    ) : StyleTabItem(type, selectedItem)

    data class Favorite(
        override val type: String = "favorite",
        override val selectedItem: String = "",
    ) : StyleTabItem(type, selectedItem)
}

/**
 * 내 스타일 수정하기
 * @param modifier
 * @param padding
 * @param type 화면 타입 - 포지션, 기술, 서브미션
 * @param onBackClick
 */
@Composable
fun ModifyMyStyleScreen(
    modifier: Modifier,
    padding: PaddingValues,
    type: String,
    onCompleteClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel = hiltViewModel<ProfileViewModel>()

    /** Screen Info **/
    val isTypeNeedSelectAll = type == SkillStyleScreenType.ALL.screenName
    var screenType by remember { mutableStateOf<SkillStyleScreenType>(SkillStyleScreenType.ALL) }
    var screenTitleRes by remember { mutableStateOf<Int?>(null) }
    var screenSelectedItem by remember { mutableStateOf("") }
    var screenIndicatorItems by remember { mutableStateOf<List<StyleCardIndicator>>(listOf()) }

    /** Tab Index ( 0 == Best, 1 == Favorite ) **/
    var styleTabIndex by remember { mutableIntStateOf(0) }
    var bestTabTitle by remember { mutableStateOf<String?>(null) }
    var favoriteTabTitle by remember { mutableStateOf<String?>(null) }

    // TODO chan type == SkillStyleScreenType.ALL 일때는 특정 Flag 값으로 수정하고 모든경우를 pageIndex로 구분해야할까?
    // TODO chan 여기저기 구분값 다름 type == SkillStyleScreenType 으로 구분하다가 ALL인경우 pageIndex로 구분해보니 복잡도 증가
    /** Page Index ( 0==Position, 1==Technique, 2==Submission) **/
    var pageIndex by remember { mutableIntStateOf(0) }

    /** Select Position Value **/
    var bestPositionIndex by remember { mutableStateOf<Int?>(null) }
    var favoritePositionIndex by remember { mutableStateOf<Int?>(null) }

    /** Select Technique Value **/
    var bestTechniqueIndex by remember { mutableStateOf<Int?>(null) }
    var favoriteTechniqueIndex by remember { mutableStateOf<Int?>(null) }

    /** Select Submission Value **/
    var bestSubmissionIndex by remember { mutableStateOf<Int?>(null) }
    var favoriteSubmissionIndex by remember { mutableStateOf<Int?>(null) }

    val cardShape = RoundedCornerShape(16.dp)

    val configuration = LocalConfiguration.current
    val targetHeightDp = (configuration.screenHeightDp * 0.5).dp

    /** 뒤로가기 컨트롤 **/
    val onHistoryBackClick: () -> Unit = {
        if (isTypeNeedSelectAll) {
            // ALL > 포지션, 기술, 서브미션 모두 선택 > 시작은 포지션부터
            when (pageIndex) {
                1 -> { pageIndex = 0 }
                2 -> { pageIndex = 1 }
                else -> { onBackClick() }
            }
        } else {
            onBackClick()
        }
    }

    /** 상단 탭 타이틀  **/
    fun setTabTitle() {
        when(pageIndex) {
            0 -> { // POSITION
                bestTabTitle = POSITION_LIST[bestPositionIndex?:0].displayName
                if (styleTabIndex == 0) {
                    favoriteTabTitle = if(favoritePositionIndex == null) "입력해주세요" else POSITION_LIST[favoritePositionIndex?:0].displayName
                } else {
                    favoriteTabTitle = POSITION_LIST[favoritePositionIndex?:0].displayName
                    if (favoritePositionIndex == null)
                        favoritePositionIndex = 0
                }
            }
            1 -> { // TECHNIQUE
                bestTabTitle = TECHNIQUE_LIST[bestTechniqueIndex?:0].displayName
                if (styleTabIndex == 0) {
                    favoriteTabTitle = if(favoriteTechniqueIndex == null) "입력해주세요" else TECHNIQUE_LIST[favoriteTechniqueIndex?:0].displayName
                } else {
                    favoriteTabTitle = TECHNIQUE_LIST[favoriteTechniqueIndex?:0].displayName
                    if (favoriteTechniqueIndex == null)
                        favoriteTechniqueIndex = 0
                }
            }
            2 -> { // SUBMISSION
                bestTabTitle = SUBMISSION_LIST[bestSubmissionIndex?:0].displayName
                if (styleTabIndex == 0) {
                    favoriteTabTitle = if(favoriteSubmissionIndex == null) "입력해주세요" else SUBMISSION_LIST[favoriteSubmissionIndex?:0].displayName
                } else {
                    favoriteTabTitle = SUBMISSION_LIST[favoriteSubmissionIndex?:0].displayName
                    if (favoriteSubmissionIndex == null)
                        favoriteSubmissionIndex = 0
                }
            }
        }
    }

    /** 나중에 하기 버튼 클릭 **/
    val onSkipBtnClick: () -> Unit = {
        when (pageIndex) {
            0 -> { // POSITION

            }
            1 -> { // TECHNIQUE

            }
            2 -> { // SUBMISSION

            }
        }
    }

    /** 하단 버튼 컨트롤 **/
    // TODO chan 하단 버튼 누름 > REST API Success > Next Step
    val onBottomBtnClick: () -> Unit = {
        if (isTypeNeedSelectAll) {
            // ALL > 포지션, 기술, 서브미션 모두 선택 > 시작은 포지션부터
            when (pageIndex) {
                0 -> { pageIndex = 1 }
                1 -> { pageIndex = 2 }
                2 -> {
                    viewModel.updateProfileMyStyle(
                        bestPositionIndex = bestPositionIndex ?: 0,
                        favoritePositionIndex = favoritePositionIndex ?: 0,
                        bestTechniqueIndex = bestTechniqueIndex ?: 0,
                        favoriteTechniqueIndex = favoriteTechniqueIndex ?: 0,
                        bestSubmissionIndex = bestSubmissionIndex ?: 0,
                        favoriteSubmissionIndex = favoriteSubmissionIndex ?: 0,
                    )
                }
            }
        } else {
            // TODO chan 각각 어떤 스크린의 값인지 구분 필요
            viewModel.updateProfileMyStyle(
                bestPositionIndex = bestPositionIndex ?: 0,
                favoritePositionIndex = favoritePositionIndex ?: 0,
                bestTechniqueIndex = bestTechniqueIndex ?: 0,
                favoriteTechniqueIndex = favoriteTechniqueIndex ?: 0,
                bestSubmissionIndex = bestSubmissionIndex ?: 0,
                favoriteSubmissionIndex = favoriteSubmissionIndex ?: 0,
            )
        }
    }

    /** 카드 선택 콜백 **/
    val onSelectedCardItem: (index: Int) -> Unit = { index ->
        when (pageIndex) {
            0 -> {
                if (styleTabIndex == 0) {
                    bestPositionIndex = index
                } else {
                    favoritePositionIndex = index
                }
            }
            1 -> {
                if (styleTabIndex == 0) {
                    bestTechniqueIndex = index
                } else {
                    favoriteTechniqueIndex = index
                }
            }
            2 -> {
                if (styleTabIndex == 0) {
                    bestSubmissionIndex = index
                } else {
                    favoriteSubmissionIndex = index
                }
            }
        }
        setTabTitle()
    }

    /** 카드 배경 반환 **/
    fun returnCardBgRes (): Int = when(pageIndex) {
        0 -> { // POSITION
            val itemIndex = if (styleTabIndex==0) bestPositionIndex else favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].getBgDrawableRes()
        }

        1 -> { // TECHNIQUE
            val itemIndex = if (styleTabIndex==0) bestTechniqueIndex else favoriteTechniqueIndex
            TECHNIQUE_LIST[itemIndex ?: 0].getBgDrawableRes()
        }

        2 -> { // SUBMISSION
            val itemIndex = if (styleTabIndex==0) bestSubmissionIndex else favoriteSubmissionIndex
            SUBMISSION_LIST[itemIndex ?: 0].getBgDrawableRes()
        }

        else -> {
            val itemIndex = if (styleTabIndex==0) bestPositionIndex else favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].getBgDrawableRes()
        }
    }

    /** 카드 타입 반환 **/
    fun returnCardType(): String = when(pageIndex) {
        // POSITION
        0 -> "포지션"
        // TECHNIQUE
        1 -> "기술"
        // SUBMISSION
        2 -> "서브 미션"
        else -> ""
    }

    /** 카드 이름 반환 **/
    fun returnCardTitle (): String = when(pageIndex) {
        0 -> { // POSITION
            val itemIndex = if (styleTabIndex==0) bestPositionIndex else favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].displayName
        }

        1 -> { // TECHNIQUE
            val itemIndex = if (styleTabIndex==0) bestTechniqueIndex else favoriteTechniqueIndex
            TECHNIQUE_LIST[itemIndex ?: 0].displayName
        }

        2 -> { // SUBMISSION
            val itemIndex = if (styleTabIndex==0) bestSubmissionIndex else favoriteSubmissionIndex
            SUBMISSION_LIST[itemIndex ?: 0].displayName
        }

        else -> {
            val itemIndex = if (styleTabIndex==0) bestPositionIndex else favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].displayName
        }
    }

    /** 카드 설명 반환 **/
    fun returnCardInfo (): String = when(pageIndex) {
        0 -> { // POSITION
            val itemIndex = if (styleTabIndex==0) bestPositionIndex else favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].cardInfo
        }

        1 -> { // TECHNIQUE
            val itemIndex = if (styleTabIndex==0) bestTechniqueIndex else favoriteTechniqueIndex
            TECHNIQUE_LIST[itemIndex ?: 0].cardInfo
        }

        2 -> { // SUBMISSION
            val itemIndex = if (styleTabIndex==0) bestSubmissionIndex else favoriteSubmissionIndex
            SUBMISSION_LIST[itemIndex ?: 0].cardInfo
        }

        else -> {
            val itemIndex = if (styleTabIndex==0) bestPositionIndex else favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].cardInfo
        }
    }

    /** 카드 아이콘 반환 **/
    fun returnCardIconRes (): Int = when(pageIndex) {
        0 -> { // POSITION
            val itemIndex = if (styleTabIndex==0) bestPositionIndex else favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].getIconDrawableRes()
        }

        1 -> { // TECHNIQUE
            val itemIndex = if (styleTabIndex==0) bestTechniqueIndex else favoriteTechniqueIndex
            TECHNIQUE_LIST[itemIndex ?: 0].getIconDrawableRes()
        }

        2 -> { // SUBMISSION
            val itemIndex = if (styleTabIndex==0) bestSubmissionIndex else favoriteSubmissionIndex
            SUBMISSION_LIST[itemIndex ?: 0].getIconDrawableRes()
        }

        else -> {
            val itemIndex = if (styleTabIndex==0) bestPositionIndex else favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].getIconDrawableRes()
        }
    }

    LaunchedEffect(Unit) {
        // 스크린 타입 - 포지션, 기술, 서브미션
        when (type) {
            SkillStyleScreenType.Position.screenName -> {
                pageIndex = 0
            }

            SkillStyleScreenType.Technique.screenName -> {
                pageIndex = 1
            }

            SkillStyleScreenType.Submission.screenName -> {
                pageIndex = 2
            }

            else -> {
                // ALL > 포지션, 기술, 서브미션 모두 선택 > 시작은 포지션부터
                pageIndex = 0
            }
        }
        setTabTitle()
    }

    LaunchedEffect(pageIndex) {
        pageIndex?.let { index ->
            when (index) {
                0 -> {
                    // Position
                    styleTabIndex = 0
                    screenTitleRes = R.string.profile_my_style_position_title
                    screenIndicatorItems = POSITION_INDICATOR_LIST
                }

                1 -> {
                    // Technique
                    styleTabIndex = 0
                    screenTitleRes = R.string.profile_my_style_technique_title
                    screenIndicatorItems = TECHNIQUE_INDICATOR_LIST
                }

                2 -> {
                    // Submission
                    styleTabIndex = 0
                    screenTitleRes = R.string.profile_my_style_submission_title
                    screenIndicatorItems = SUBMISSION_INDICATOR_LIST
                }
            }
            setTabTitle()
        }
    }

    LaunchedEffect(viewModel.profileUiState) {
        val uiState = viewModel.profileUiState
        when(uiState) {
            is UiState.Success -> {
                onCompleteClick()
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
        color = White,
    ) {
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }

                item {
                    // 특기/최애 탭
                    SegmentedPositionTabBar(
                        modifier = Modifier.fillMaxWidth(),
                        tabs = listOf(
                            StyleTabItem.Best(selectedItem = bestTabTitle?:"입력해주세요"),
                            StyleTabItem.Favorite(selectedItem = favoriteTabTitle?:"입력해주세요"),
                        ),
                        selectedIndex = styleTabIndex,
                        onTabSelected = { index ->
                            styleTabIndex = index
                            if (index == 1 && favoriteTabTitle == "입력해주세요")
                                setTabTitle()
                        }
                    )
                }

                item {
                    // Selected Card
                    key(styleTabIndex, pageIndex) {
                        DraggableFlipCard(
                            modifier = Modifier
                                .height(targetHeightDp)
                                .aspectRatio(10f / 16f)
                                .clip(cardShape),

                            front = {
                                CardFrontLayout(
                                    modifier = Modifier.fillMaxSize(),
                                    cardShape = cardShape,
                                    backgroundRes = returnCardBgRes(),
                                    iconRes = returnCardIconRes(),
                                    title = returnCardTitle(),
                                    info = returnCardInfo(),
                                )
                            },
                            back = {
                                CardBackLayout(
                                    modifier = Modifier.fillMaxSize(),
                                    cardShape = cardShape,
                                    type = returnCardType(),
                                    title = returnCardTitle(),
                                )
                            }
                        )
                    }
                }

                item {
                    // Card Indicator
                    CardIndicatorLayout(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp),
                        items = screenIndicatorItems,
                        selectedIndex = when (pageIndex) {
                            0 -> {
                                if (styleTabIndex == 0) bestPositionIndex ?: 0 else favoritePositionIndex ?: 0
                            }
                            1 -> {
                                if (styleTabIndex == 0) bestTechniqueIndex ?: 0 else favoriteTechniqueIndex ?: 0
                            }
                            2 -> {
                                if (styleTabIndex == 0) bestSubmissionIndex ?: 0 else favoriteSubmissionIndex ?: 0
                            }
                            else -> 0
                        },
                        onTabSelected = onSelectedCardItem
                    )
                }

                item {
                    Box(
                        modifier = Modifier
                            .height(800.dp)
                            .fillMaxWidth()
                            .background(color = Blue500)
                    )
                }
            }

            // Top App Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = WhiteOpacity40)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .paint(
                                painter = painterResource(com.kyu.jiu_jitsu.ui.R.drawable.ic_gray_left_back_button),
                                contentScale = ContentScale.Crop,
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true),
                                role = Role.Button,
                                onClick = onHistoryBackClick
                            )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (isTypeNeedSelectAll) {
                        TintButton(
                            text = "나중에 하기",
                            onClick = {}
                        )
                    } else {

                    }
                }
                // Center Title
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 48.dp),
                    text = stringResource(
                        screenTitleRes ?: R.string.profile_my_style_position_title
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    color = ColorComponents.Header.Header.Text,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Bottom Button
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
            ) {
                Spacer(
                    modifier = Modifier
                        .height(25.dp)
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    TrueWhite.copy(alpha = 0f),
                                    TrueWhite.copy(alpha = 0.9f)
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .background(color = TrueWhite)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 10.dp)
                ) {
                    PrimaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        text = "Bottom Button",
                        onClick = onBottomBtnClick
                    )
                }
            }
        }
    }
}