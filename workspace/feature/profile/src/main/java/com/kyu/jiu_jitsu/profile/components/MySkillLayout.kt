package com.kyu.jiu_jitsu.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.data.model.CommunityProfileInfo
import com.kyu.jiu_jitsu.data.model.POSITION
import com.kyu.jiu_jitsu.data.model.SUBMISSION
import com.kyu.jiu_jitsu.data.model.TECHNIQUE
import com.kyu.jiu_jitsu.profile.R
import com.kyu.jiu_jitsu.ui.components.button.TintButton
import com.kyu.jiu_jitsu.ui.theme.Blue500
import com.kyu.jiu_jitsu.ui.theme.CoolGray75
import com.kyu.jiu_jitsu.ui.theme.getIconDrawableRes

/**
 * 커뮤니티 프로필 정보 - 나의 포지션, 서브미션, 기술
 * @param info 커뮤니티 프로필 정보
 * @param onSaveMyStyleClick 내 스타일 등록하기
 */
@Composable
fun MySkillLayout(
    info: CommunityProfileInfo? = null,
    onSaveMyStyleClick: () -> Unit = {},
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
                onSaveMyStyleClick = onSaveMyStyleClick
            )
        }
    }
}

/** 내 스타일 등록하기 (등록된 정보 없음) */
@Composable
private fun DefaultCommunityProfileInfo(
    onSaveMyStyleClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(62.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp),
            text = stringResource(R.string.profile_my_style_title),
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp),
            text = stringResource(R.string.profile_my_style_sub_title),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

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
            /** 내 스타일 등록하기 버튼 */
            TintButton(
                text = stringResource(R.string.profile_my_style_button),
                onClick = onSaveMyStyleClick,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(R.drawable.ic_my_style_default),
                contentDescription = "MyStyleDefault"
            )
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

/** 내 스타일 리스트 */
@Composable
private fun CommunityProfileInfoList(
    info: CommunityProfileInfo,
    onSaveMyStyleClick: () -> Unit = {},
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column {
            PositionInfo(
                info.bestPosition,
                info.favoritePosition,
                onSaveMyStyleClick = onSaveMyStyleClick
            )
            TechniqueInfo(
                info.bestTechnique,
                info.favoriteTechnique,
                onSaveMyStyleClick = onSaveMyStyleClick
            )
            SubmissionInfo(
                info.bestSubmission,
                info.favoriteSubmission,
                onSaveMyStyleClick = onSaveMyStyleClick
            )
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
    onSaveMyStyleClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier.padding(vertical = 15.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(com.kyu.jiu_jitsu.ui.R.string.common_position),
            style = MaterialTheme.typography.titleMedium,
            color = CoolGray75
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            StyleCardItem(
                modifier = Modifier.weight(1f).padding(start = 20.dp),
                iconRes = bestPosition?.getIconDrawableRes(),
                titleRes = com.kyu.jiu_jitsu.ui.R.string.common_my_best,
                itemName = bestPosition?.name ?: stringResource(com.kyu.jiu_jitsu.ui.R.string.common_item_null),
                onClickEvent = onSaveMyStyleClick
            )
            Spacer(modifier = Modifier.width(10.dp))
            StyleCardItem(
                modifier = Modifier.weight(1f).padding(end = 20.dp),
                iconRes = favoritePosition?.getIconDrawableRes(),
                titleRes = com.kyu.jiu_jitsu.ui.R.string.common_my_favorite,
                itemName = favoritePosition?.name ?: stringResource(com.kyu.jiu_jitsu.ui.R.string.common_item_null),
                onClickEvent = onSaveMyStyleClick
            )
        }
    }
}

/** 나의 테크닉 스타일
 *  @param bestTechnique 최고 테크닉
 *  @param favoriteTechnique 최애 테크닉
 */
@Composable
private fun TechniqueInfo(
    bestTechnique: TECHNIQUE?,
    favoriteTechnique: TECHNIQUE?,
    onSaveMyStyleClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier.padding(vertical = 15.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(com.kyu.jiu_jitsu.ui.R.string.common_technique),
            style = MaterialTheme.typography.titleMedium,
            color = CoolGray75
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            StyleCardItem(
                modifier = Modifier.weight(1f).padding(start = 20.dp),
                iconRes = bestTechnique?.getIconDrawableRes(),
                titleRes = com.kyu.jiu_jitsu.ui.R.string.common_my_best,
                itemName = bestTechnique?.name ?: stringResource(com.kyu.jiu_jitsu.ui.R.string.common_item_null),
                onClickEvent = onSaveMyStyleClick
            )
            Spacer(modifier = Modifier.width(10.dp))
            StyleCardItem(
                modifier = Modifier.weight(1f).padding(end = 20.dp),
                iconRes = favoriteTechnique?.getIconDrawableRes(),
                titleRes = com.kyu.jiu_jitsu.ui.R.string.common_my_favorite,
                itemName = favoriteTechnique?.name ?: stringResource(com.kyu.jiu_jitsu.ui.R.string.common_item_null),
                onClickEvent = onSaveMyStyleClick
            )
        }
    }
}

/** 나의 테크닉 스타일
 *  @param bestTechnique 최고 테크닉
 *  @param favoriteTechnique 최애 테크닉
 */
@Composable
private fun SubmissionInfo(
    bestSubmission: SUBMISSION?,
    favoriteSubmission: SUBMISSION?,
    onSaveMyStyleClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier.padding(vertical = 15.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(com.kyu.jiu_jitsu.ui.R.string.common_submission),
            style = MaterialTheme.typography.titleMedium,
            color = CoolGray75
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            StyleCardItem(
                modifier = Modifier.weight(1f).padding(start = 20.dp),
                iconRes = bestSubmission?.getIconDrawableRes(),
                titleRes = com.kyu.jiu_jitsu.ui.R.string.common_my_best,
                itemName = bestSubmission?.name ?: stringResource(com.kyu.jiu_jitsu.ui.R.string.common_item_null),
                onClickEvent = onSaveMyStyleClick
            )
            Spacer(modifier = Modifier.width(10.dp))
            StyleCardItem(
                modifier = Modifier.weight(1f).padding(end = 20.dp),
                iconRes = favoriteSubmission?.getIconDrawableRes(),
                titleRes = com.kyu.jiu_jitsu.ui.R.string.common_my_favorite,
                itemName = favoriteSubmission?.name ?: stringResource(com.kyu.jiu_jitsu.ui.R.string.common_item_null),
                onClickEvent = onSaveMyStyleClick
            )
        }
    }
}