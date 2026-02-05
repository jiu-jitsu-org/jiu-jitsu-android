package com.kyu.jiu_jitsu.profile.screen

import android.annotation.SuppressLint
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
import com.kyu.jiu_jitsu.profile.viewmodel.ModifyMyStyleAction
import com.kyu.jiu_jitsu.profile.viewmodel.ModifyMyStyleViewModel
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
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ModifyMyStyleScreen(
    modifier: Modifier,
    padding: PaddingValues,
    type: String,
    onCompleteClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel = hiltViewModel<ModifyMyStyleViewModel>()

    /** Screen Info **/
    var screenTitleRes by remember { mutableStateOf<Int?>(null) }
    var screenIndicatorItems by remember { mutableStateOf<List<StyleCardIndicator>>(listOf()) }
    /** Card Shape **/
    val cardShape = RoundedCornerShape(16.dp)

    val configuration = LocalConfiguration.current
    val targetHeightDp = (configuration.screenHeightDp * 0.5).dp

    /** 카드 배경 반환 **/
    fun returnCardBgRes (): Int = when(viewModel.pageIndex) {
        0 -> { // POSITION
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestPositionIndex else viewModel.favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].getBgDrawableRes()
        }

        1 -> { // TECHNIQUE
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestTechniqueIndex else viewModel.favoriteTechniqueIndex
            TECHNIQUE_LIST[itemIndex ?: 0].getBgDrawableRes()
        }

        2 -> { // SUBMISSION
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestSubmissionIndex else viewModel.favoriteSubmissionIndex
            SUBMISSION_LIST[itemIndex ?: 0].getBgDrawableRes()
        }

        else -> {
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestPositionIndex else viewModel.favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].getBgDrawableRes()
        }
    }

    /** 카드 타입 반환 **/
    fun returnCardType(): String = when(viewModel.pageIndex) {
        // POSITION
        0 -> "포지션"
        // TECHNIQUE
        1 -> "기술"
        // SUBMISSION
        2 -> "서브 미션"
        else -> ""
    }

    /** 카드 이름 반환 **/
    fun returnCardTitle (): String = when(viewModel.pageIndex) {
        0 -> { // POSITION
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestPositionIndex else viewModel.favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].displayName
        }

        1 -> { // TECHNIQUE
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestTechniqueIndex else viewModel.favoriteTechniqueIndex
            TECHNIQUE_LIST[itemIndex ?: 0].displayName
        }

        2 -> { // SUBMISSION
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestSubmissionIndex else viewModel.favoriteSubmissionIndex
            SUBMISSION_LIST[itemIndex ?: 0].displayName
        }

        else -> {
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestPositionIndex else viewModel.favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].displayName
        }
    }

    /** 카드 설명 반환 **/
    fun returnCardInfo (): String = when(viewModel.pageIndex) {
        0 -> { // POSITION
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestPositionIndex else viewModel.favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].cardInfo
        }

        1 -> { // TECHNIQUE
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestTechniqueIndex else viewModel.favoriteTechniqueIndex
            TECHNIQUE_LIST[itemIndex ?: 0].cardInfo
        }

        2 -> { // SUBMISSION
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestSubmissionIndex else viewModel.favoriteSubmissionIndex
            SUBMISSION_LIST[itemIndex ?: 0].cardInfo
        }

        else -> {
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestPositionIndex else viewModel.favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].cardInfo
        }
    }

    /** 카드 아이콘 반환 **/
    fun returnCardIconRes (): Int = when(viewModel.pageIndex) {
        0 -> { // POSITION
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestPositionIndex else viewModel.favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].getIconDrawableRes()
        }

        1 -> { // TECHNIQUE
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestTechniqueIndex else viewModel.favoriteTechniqueIndex
            TECHNIQUE_LIST[itemIndex ?: 0].getIconDrawableRes()
        }

        2 -> { // SUBMISSION
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestSubmissionIndex else viewModel.favoriteSubmissionIndex
            SUBMISSION_LIST[itemIndex ?: 0].getIconDrawableRes()
        }

        else -> {
            val itemIndex = if (viewModel.styleTabIndex==0) viewModel.bestPositionIndex else viewModel.favoritePositionIndex
            POSITION_LIST[itemIndex ?: 0].getIconDrawableRes()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initScreenType(type)
    }

    LaunchedEffect(viewModel.pageIndex) {
        viewModel.styleTabIndex = 0
        when (viewModel.pageIndex) {
            0 -> {
                // Position
                screenTitleRes = R.string.profile_my_style_position_title
                screenIndicatorItems = POSITION_INDICATOR_LIST
            }

            1 -> {
                // Technique
                screenTitleRes = R.string.profile_my_style_technique_title
                screenIndicatorItems = TECHNIQUE_INDICATOR_LIST
            }

            2 -> {
                // Submission
                screenTitleRes = R.string.profile_my_style_submission_title
                screenIndicatorItems = SUBMISSION_INDICATOR_LIST
            }
        }
        viewModel.setTabTitle()
    }

    LaunchedEffect(viewModel.profileUiState) {
        val uiState = viewModel.profileUiState
        when(uiState) {
            is UiState.Success -> {
//                onCompleteClick()
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
                            StyleTabItem.Best(selectedItem = viewModel.bestTabTitle?:"입력해주세요"),
                            StyleTabItem.Favorite(selectedItem = viewModel.favoriteTabTitle?:"입력해주세요"),
                        ),
                        selectedIndex = viewModel.styleTabIndex,
                        onTabSelected = { index ->
                            viewModel.styleTabIndex = index
                            if (index == 1 && viewModel.favoriteTabTitle == "입력해주세요")
                                viewModel.setTabTitle()
                        }
                    )
                }

                item {
                    // Selected Card
                    key(viewModel.styleTabIndex, viewModel.pageIndex) {
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
                        selectedIndex = when (viewModel.pageIndex) {
                            0 -> {
                                if (viewModel.styleTabIndex == 0) viewModel.bestPositionIndex ?: 0 else viewModel.favoritePositionIndex ?: 0
                            }
                            1 -> {
                                if (viewModel.styleTabIndex == 0) viewModel.bestTechniqueIndex ?: 0 else viewModel.favoriteTechniqueIndex ?: 0
                            }
                            2 -> {
                                if (viewModel.styleTabIndex == 0) viewModel.bestSubmissionIndex ?: 0 else viewModel.favoriteSubmissionIndex ?: 0
                            }
                            else -> 0
                        },
                        onTabSelected = { index -> viewModel.onAction(ModifyMyStyleAction.CardItemSelected(index)) }
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
                                onClick = {
                                    viewModel.onAction(ModifyMyStyleAction.BackClicked(onBackClick))
                                }
                            )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (viewModel.screenType is SkillStyleScreenType.ALL) {
                        TintButton(
                            text = "나중에 하기",
                            onClick = { viewModel.onAction(ModifyMyStyleAction.SkipClicked(onCompleteClick)) }
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
                        onClick = { viewModel.onAction(ModifyMyStyleAction.BottomBtnClicked(onCompleteClick)) }
                    )
                }
            }
        }
    }
}