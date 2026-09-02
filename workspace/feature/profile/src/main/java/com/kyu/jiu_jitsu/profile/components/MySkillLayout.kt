package com.kyu.jiu_jitsu.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.model.CommunityProfileInfo
import com.kyu.jiu_jitsu.model.POSITION
import com.kyu.jiu_jitsu.model.SUBMISSION
import com.kyu.jiu_jitsu.model.TECHNIQUE
import com.kyu.jiu_jitsu.profile.R
import com.kyu.jiu_jitsu.ui.components.button.TintButton
import com.kyu.jiu_jitsu.ui.routes.SkillStyleScreenType
import com.kyu.jiu_jitsu.ui.theme.Blue500
import com.kyu.jiu_jitsu.ui.theme.CoolGray75
import com.kyu.jiu_jitsu.ui.theme.getIconDrawableRes

private val MySkillContentMaxWidth = 560.dp
private const val CommunityProfileCardAspectRatio = 253f / 197f

/**
 * 커뮤니티 프로필 정보 - 나의 포지션, 서브미션, 기술
 * @param info 커뮤니티 프로필 정보
 * @param onSaveMyStyleClick 내 스타일 등록하기
 */
@Composable
fun MySkillLayout(
    info: CommunityProfileInfo? = null,
    onSaveMyStyleClick: (screenType: String) -> Unit = {},
) {
    if (info == null) {
        DefaultCommunityProfileInfo(
            onSaveMyStyleClick = onSaveMyStyleClick
        )
    } else {
        if (info.bestPosition == null && info.favoritePosition == null
            && info.bestTechnique == null && info.favoriteTechnique == null
            && info.bestSubmission == null && info.favoriteSubmission == null) {
            DefaultCommunityProfileInfo(
                onSaveMyStyleClick = onSaveMyStyleClick
            )
        } else {
            CommunityProfileInfoList(
                info,
                onModifyMyStyleClick = onSaveMyStyleClick
            )
        }
    }
}

/** 내 스타일 등록하기 (등록된 정보 없음) */
@Composable
private fun DefaultCommunityProfileInfo(
    onSaveMyStyleClick: (screenType: String) -> Unit = {},
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val contentWidth = maxWidth.coerceAtMost(MySkillContentMaxWidth)
        val topPadding = (contentWidth * 0.14f).coerceIn(48.dp, 72.dp)
        val titleHorizontalPadding = (contentWidth * 0.06f).coerceIn(20.dp, 32.dp)
        val subtitleButtonSpacing = (contentWidth * 0.08f).coerceIn(28.dp, 48.dp)
        val buttonImageSpacing = (contentWidth * 0.07f).coerceIn(24.dp, 40.dp)
        val buttonWidth = (contentWidth * 0.52f)
            .coerceIn(200.dp, 320.dp)
            .coerceAtMost(contentWidth - titleHorizontalPadding * 2)
        val imageWidth = (contentWidth * 0.92f).coerceAtMost(430.dp)
        val bottomPadding = (contentWidth * 0.10f).coerceIn(36.dp, 56.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(topPadding))
            Text(
                modifier = Modifier
                    .width(contentWidth)
                    .padding(horizontal = titleHorizontalPadding),
                text = stringResource(R.string.profile_my_style_title),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .width(contentWidth)
                    .padding(horizontal = titleHorizontalPadding),
                text = stringResource(R.string.profile_my_style_sub_title),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(subtitleButtonSpacing))

            /** 내 스타일 등록하기 버튼 */
            TintButton(
                modifier = Modifier.width(buttonWidth),
                text = stringResource(R.string.profile_my_style_button),
                onClick = { onSaveMyStyleClick(SkillStyleScreenType.ALL.screenName) },
            )
            Spacer(modifier = Modifier.height(buttonImageSpacing))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Blue500.copy(alpha = 0f),
                                Blue500.copy(alpha = 0.4f)
                            )
                        )
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.width(imageWidth),
                    painter = painterResource(R.drawable.ic_my_style_default),
                    contentDescription = "MyStyleDefault",
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(bottomPadding))
            }
        }
    }
}

/** 내 스타일 리스트 */
@Composable
private fun CommunityProfileInfoList(
    info: CommunityProfileInfo,
    onModifyMyStyleClick: (screenType: String) -> Unit = {},
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        color = Color.Transparent
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val horizontalPadding = (maxWidth * 0.046f).coerceIn(20.dp, 28.dp)
            val contentWidth = (maxWidth - horizontalPadding * 2).coerceAtMost(MySkillContentMaxWidth)
            val cardGap = (contentWidth * 0.024f).coerceIn(10.dp, 16.dp)
            val cardWidth = (contentWidth - cardGap) / 2
            val cardHeight = (cardWidth / CommunityProfileCardAspectRatio).coerceIn(132.dp, 202.dp)
            val topPadding = (contentWidth * 0.085f).coerceIn(30.dp, 50.dp)
            val titleCardSpacing = (contentWidth * 0.032f).coerceIn(12.dp, 20.dp)
            val sectionSpacing = (contentWidth * 0.11f).coerceIn(34.dp, 58.dp)
            val bottomPadding = (contentWidth * 0.05f).coerceIn(16.dp, 28.dp)

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(contentWidth)
                    .padding(top = topPadding, bottom = bottomPadding)
            ) {
                PositionInfo(
                    bestPosition = info.bestPosition,
                    favoritePosition = info.favoritePosition,
                    cardHeight = cardHeight,
                    cardGap = cardGap,
                    titleCardSpacing = titleCardSpacing,
                    onSaveMyStyleClick = { onModifyMyStyleClick(SkillStyleScreenType.Position.screenName) }
                )
                Spacer(modifier = Modifier.height(sectionSpacing))
                TechniqueInfo(
                    bestTechnique = info.bestTechnique,
                    favoriteTechnique = info.favoriteTechnique,
                    cardHeight = cardHeight,
                    cardGap = cardGap,
                    titleCardSpacing = titleCardSpacing,
                    onSaveMyStyleClick = { onModifyMyStyleClick(SkillStyleScreenType.Technique.screenName) }
                )
                Spacer(modifier = Modifier.height(sectionSpacing))
                SubmissionInfo(
                    bestSubmission = info.bestSubmission,
                    favoriteSubmission = info.favoriteSubmission,
                    cardHeight = cardHeight,
                    cardGap = cardGap,
                    titleCardSpacing = titleCardSpacing,
                    onSaveMyStyleClick = { onModifyMyStyleClick(SkillStyleScreenType.Submission.screenName) }
                )
            }
        }
    }
}

/** 나의 포지션 스타일
 *  @param bestPosition 최고 포지션
 *  @param favoritePosition 최애 포지션
 */
@Composable
private fun PositionInfo(
    bestPosition: POSITION?,
    favoritePosition: POSITION?,
    cardHeight: Dp,
    cardGap: Dp,
    titleCardSpacing: Dp,
    onSaveMyStyleClick: () -> Unit = {},
) {
    StyleInfoSection(
        titleRes = com.kyu.jiu_jitsu.ui.R.string.common_position,
        bestIconRes = bestPosition?.getIconDrawableRes(),
        bestItemName = bestPosition?.displayName?.toSingleLine(),
        favoriteIconRes = favoritePosition?.getIconDrawableRes(),
        favoriteItemName = favoritePosition?.displayName?.toSingleLine(),
        cardHeight = cardHeight,
        cardGap = cardGap,
        titleCardSpacing = titleCardSpacing,
        onSaveMyStyleClick = onSaveMyStyleClick,
    )
}

/** 나의 테크닉 스타일
 *  @param bestTechnique 최고 테크닉
 *  @param favoriteTechnique 최애 테크닉
 */
@Composable
private fun TechniqueInfo(
    bestTechnique: TECHNIQUE?,
    favoriteTechnique: TECHNIQUE?,
    cardHeight: Dp,
    cardGap: Dp,
    titleCardSpacing: Dp,
    onSaveMyStyleClick: () -> Unit = {},
) {
    StyleInfoSection(
        titleRes = com.kyu.jiu_jitsu.ui.R.string.common_technique,
        bestIconRes = bestTechnique?.getIconDrawableRes(),
        bestItemName = bestTechnique?.displayName?.toSingleLine(),
        favoriteIconRes = favoriteTechnique?.getIconDrawableRes(),
        favoriteItemName = favoriteTechnique?.displayName?.toSingleLine(),
        cardHeight = cardHeight,
        cardGap = cardGap,
        titleCardSpacing = titleCardSpacing,
        onSaveMyStyleClick = onSaveMyStyleClick,
    )
}

/** 나의 서브미션 스타일
 *  @param bestSubmission 최고 서브미션
 *  @param favoriteSubmission 최애 서브미션
 */
@Composable
private fun SubmissionInfo(
    bestSubmission: SUBMISSION?,
    favoriteSubmission: SUBMISSION?,
    cardHeight: Dp,
    cardGap: Dp,
    titleCardSpacing: Dp,
    onSaveMyStyleClick: () -> Unit = {},
) {
    StyleInfoSection(
        titleRes = com.kyu.jiu_jitsu.ui.R.string.common_submission,
        bestIconRes = bestSubmission?.getIconDrawableRes(),
        bestItemName = bestSubmission?.displayName?.toSingleLine(),
        favoriteIconRes = favoriteSubmission?.getIconDrawableRes(),
        favoriteItemName = favoriteSubmission?.displayName?.toSingleLine(),
        cardHeight = cardHeight,
        cardGap = cardGap,
        titleCardSpacing = titleCardSpacing,
        onSaveMyStyleClick = onSaveMyStyleClick,
    )
}

@Composable
private fun StyleInfoSection(
    titleRes: Int,
    bestIconRes: Int?,
    bestItemName: String?,
    favoriteIconRes: Int?,
    favoriteItemName: String?,
    cardHeight: Dp,
    cardGap: Dp,
    titleCardSpacing: Dp,
    onSaveMyStyleClick: () -> Unit = {},
) {
    val emptyItemName = stringResource(com.kyu.jiu_jitsu.ui.R.string.common_item_null)

    Column {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleMedium,
            color = CoolGray75
        )
        Spacer(modifier = Modifier.height(titleCardSpacing))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(cardGap)
        ) {
            StyleCardItem(
                modifier = Modifier
                    .weight(1f)
                    .height(cardHeight),
                iconRes = bestIconRes,
                titleRes = com.kyu.jiu_jitsu.ui.R.string.common_my_best,
                itemName = bestItemName ?: emptyItemName,
                onClickEvent = onSaveMyStyleClick
            )
            StyleCardItem(
                modifier = Modifier
                    .weight(1f)
                    .height(cardHeight),
                iconRes = favoriteIconRes,
                titleRes = com.kyu.jiu_jitsu.ui.R.string.common_my_favorite,
                itemName = favoriteItemName ?: emptyItemName,
                onClickEvent = onSaveMyStyleClick
            )
        }
    }
}

private fun String.toSingleLine(): String = replace("\n", " ")
