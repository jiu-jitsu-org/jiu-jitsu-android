package com.kyu.jiu_jitsu.profile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.profile.ProfileViewModel
import com.kyu.jiu_jitsu.profile.R
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
import com.kyu.jiu_jitsu.ui.components.card.DraggableFlipCard
import com.kyu.jiu_jitsu.ui.routes.SkillStyleScreenType
import com.kyu.jiu_jitsu.ui.theme.Blue500
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.TrueWhite
import com.kyu.jiu_jitsu.ui.theme.White
import com.kyu.jiu_jitsu.ui.theme.WhiteOpacity40

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
    var screenType by remember { mutableStateOf<SkillStyleScreenType>(SkillStyleScreenType.ALL) }
    var screenTitleRes by remember { mutableStateOf<Int?>(null) }
    var screenSelectedItem by remember { mutableStateOf("") }
    var screenIndicatorItems by remember { mutableStateOf<List<StyleCardIndicator>>(listOf()) }

    /** Tab Index **/
    var styleTabIndex by remember { mutableIntStateOf(0) }

    /** Page Index (case ScreenType == ALL, 0==Position, 1==Technique, 2==Submission) **/
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

    /** 뒤로가기 컨트롤 **/
    val onHistoryBackClick: () -> Unit = {
        when (type) {
            SkillStyleScreenType.ALL.screenName -> {
                // ALL > 포지션, 기술, 서브미션 모두 선택 > 시작은 포지션부터
                when (pageIndex) {
                    1 -> { pageIndex = 0 }
                    2 -> { pageIndex = 1 }
                    else -> { onBackClick() }
                }
            } else -> {
                onBackClick()
            }
        }
    }

    /** 하단 버튼 컨트롤 **/
    val onBottomBtnClick: () -> Unit = {
        when (type) {
            SkillStyleScreenType.ALL.screenName -> {
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
            } else -> {
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
    }

    LaunchedEffect(Unit) {
        // 스크린 타입 - 포지션, 기술, 서브미션
        when (type) {
            SkillStyleScreenType.Position.screenName -> {
                screenType = SkillStyleScreenType.Position
                pageIndex = 0
            }

            SkillStyleScreenType.Technique.screenName -> {
                screenType = SkillStyleScreenType.Technique
                pageIndex = 1
            }

            SkillStyleScreenType.Submission.screenName -> {
                screenType = SkillStyleScreenType.Submission
                pageIndex = 2
            }

            else -> {
                // ALL > 포지션, 기술, 서브미션 모두 선택 > 시작은 포지션부터
                screenType = SkillStyleScreenType.ALL
                pageIndex = 0
            }
        }
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
                            StyleTabItem.Best(selectedItem = "입력해주세요"),
                            StyleTabItem.Favorite(selectedItem = "입력해주세요"),
                        ),
                        selectedIndex = styleTabIndex,
                        onTabSelected = { index ->
                            styleTabIndex = index
                        }
                    )
                }

                item {
                    // Selected Card
                    DraggableFlipCard(
                        modifier = Modifier
                            .size(240.dp, 150.dp)
                            .clip(cardShape),

                        front = {
                            Card(shape = cardShape) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Front", style = MaterialTheme.typography.headlineMedium)
                                }
                            }

                        },
                        back = {
                            Card(
                                shape = cardShape,
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF243042))
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        "Back",
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }
                            }

                        }
                    )
                }

                item {
                    // Card Indicator
                    CardIndicatorLayout(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
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
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(color = WhiteOpacity40)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .paint(
                            painter = painterResource(com.kyu.jiu_jitsu.ui.R.drawable.ic_blue_left_back_button),
                            contentScale = ContentScale.Crop,
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true),
                            role = Role.Button,
                            onClick = onHistoryBackClick
                        )
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(
                        screenTitleRes ?: R.string.profile_my_style_position_title
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    color = ColorComponents.Header.Header.Text,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(36.dp))
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