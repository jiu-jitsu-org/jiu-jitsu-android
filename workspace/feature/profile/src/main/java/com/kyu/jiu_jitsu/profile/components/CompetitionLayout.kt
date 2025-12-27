package com.kyu.jiu_jitsu.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.ui.R
import com.kyu.jiu_jitsu.ui.components.button.PressableTextButton
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.CoolGray25
import com.kyu.jiu_jitsu.ui.theme.CoolGray75

/**
 * 대회 정보 Layout
 * @param competitionItemList 대회 정보 아이템 리스트
 */
@Composable
fun CompetitionLayout(
    competitionItemList: MutableList<String>? = null,
    onAddCompetitionClickEvent: () -> Unit,
    isModifyMode: Boolean = true,
) {
    Column {
        /** 타이틀 + 추가 버튼 */
        Row(modifier = Modifier.padding(horizontal = 20.dp),) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.common_competition_info),
                style = MaterialTheme.typography.titleMedium,
                color = CoolGray75
            )
            if (isModifyMode) {
                PressableTextButton(
                    text = "add",
                    onClick = onAddCompetitionClickEvent
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        /** 출전한 대회 정보 */
        if (competitionItemList != null && competitionItemList.isNotEmpty()) {
            CompetitionList(

            )
        } else {
            DefaultCompetitionList(
                onClickEvent = onAddCompetitionClickEvent
            )
        }
    }
}

@Composable
private fun DefaultCompetitionList(
    onClickEvent: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .clickable(
                role = Role.Button,
                onClick = onClickEvent,
            ),
        shadowElevation = 3.dp,
        shape = RoundedCornerShape(16.dp),
        color = ColorComponents.List.Setting.Background,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(R.drawable.ic_profile_default),
                contentDescription = "Add Competition",
                tint = CoolGray25,
            )
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(com.kyu.jiu_jitsu.profile.R.string.profile_competition_input_hint),
                style = MaterialTheme.typography.titleMedium,
                color = CoolGray75
            )
            Icon(
                painter = painterResource(R.drawable.ic_arow_right),
                contentDescription = "Add Competition",
                tint = CoolGray25,
            )
        }
    }
}

@Composable
private fun CompetitionList(

) {

}

@Composable
private fun CompetitionTile(
    title: String,
    date: String,
) {
    Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            painter = painterResource(R.drawable.ic_profile_default),
            contentDescription = "Add Competition",
            tint = CoolGray25,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(com.kyu.jiu_jitsu.profile.R.string.profile_competition_input_hint),
            style = MaterialTheme.typography.titleMedium,
            color = CoolGray75
        )
        Icon(
            painter = painterResource(R.drawable.ic_arow_right),
            contentDescription = "Add Competition",
            tint = CoolGray25,
        )
    }
}