package com.kyu.jiu_jitsu.login.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.login.R
import com.kyu.jiu_jitsu.ui.components.button.PrimaryCTAButton
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.Red500
import com.kyu.jiu_jitsu.ui.theme.Typography

sealed class SignUpAgreeType(
    @StringRes val titleResId: Int,
    var isRequired: Boolean = true,
    val code: String,
    val index: Int,
) {
    data object Service : SignUpAgreeType(
        titleResId = R.string.signup_agree_list_1,
        code = "Service",
        index = 0,
    )

    data object Privacy : SignUpAgreeType(
        titleResId = R.string.signup_agree_list_2,
        code = "Privacy",
        index = 1,
    )

    data object Age : SignUpAgreeType(
        titleResId = R.string.signup_agree_list_3,
        code = "Age",
        index = 2,
    )

    data object Marketing : SignUpAgreeType(
        titleResId = R.string.signup_agree_list_4,
        code = "Marketing",
        isRequired = false,
        index = 3,
    )
}

@Composable
fun SignUpBottomSheet(
    agreeInfoSubmit: (agreeList: List<String>) -> Unit,
) {
    val listState = rememberLazyListState()

    val agreeItemLists = remember {
        mutableStateMapOf(
            SignUpAgreeType.Service to false,
            SignUpAgreeType.Privacy to false,
            SignUpAgreeType.Age to false,
            SignUpAgreeType.Marketing to false,
        )
    }

    var isPossibleSubmit by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(
            horizontal = 15.dp,
            vertical = 15.dp,
        ),
    ) {
        // Title
        Text(
            stringResource(R.string.signup_bottom_sheet_title),
            style = Typography.titleMedium
        )
        Spacer(modifier = Modifier.height(15.dp))
        // Agree List
        LazyColumn(
            state = listState,
            userScrollEnabled = false,
        ) {
            items(
                items = agreeItemLists.keys.toList().sortedBy { it.index },
                key = { it.code },
            ) { keyItem ->
                // List Item
                SignUpAgreeItem(
                    agreeType = keyItem,
                    isSelected = agreeItemLists[keyItem]?:false,
                    onItemClick = { item ->
                        agreeItemLists[keyItem] = !(agreeItemLists[keyItem]?:false)

                        isPossibleSubmit =
                            agreeItemLists
                                .filterKeys { it != SignUpAgreeType.Marketing }
                                .all { it.value }
                    }
                )
            }
        }
        // Agree Submit CTA Button
        PrimaryCTAButton(
            text = if (isPossibleSubmit) {
                stringResource(com.kyu.jiu_jitsu.ui.R.string.common_next)
            } else {
                stringResource(R.string.signup_agree_all)
            },
            onClick = {
                if (isPossibleSubmit) {
                    agreeInfoSubmit(
                        agreeItemLists
                            .filterValues { it }
                            .map { it.key.code }
                            .toList()
                    )
                } else {
                    isPossibleSubmit = true
                    agreeItemLists.forEach { (key, _) ->
                        agreeItemLists[key] = true
                    }
                }
            }
        )
    }
}

@Composable
fun SignUpAgreeItem(
    agreeType: SignUpAgreeType,
    isSelected: Boolean = false,
    onItemClick: (SignUpAgreeType) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                role = Role.Button,
                onClick = { onItemClick(agreeType) }
            )
    ) {
        // Check Icon
        Icon(
            painter = painterResource(com.kyu.jiu_jitsu.ui.R.drawable.ic_check),
            contentDescription = "Check Icon",
            tint = if (isSelected) {
                ColorComponents.BottomSheet.Selected.ListItem.LeadingIcon
            } else {
                ColorComponents.BottomSheet.UnSelected.ListItem.LeadingIcon
            }
        )
        Spacer(modifier = Modifier.width(10.dp))
        // Agree Title
        Text(
            stringResource(agreeType.titleResId),
            style = Typography.bodySmall,
            color = if (isSelected) {
                ColorComponents.BottomSheet.Selected.ListItem.Label
            } else {
                ColorComponents.BottomSheet.UnSelected.ListItem.Label
            }
        )
        // Required or Optional
        Text(
            stringResource(
                if (agreeType.isRequired) {
                    com.kyu.jiu_jitsu.ui.R.string.common_required
                } else {
                    com.kyu.jiu_jitsu.ui.R.string.common_optional
                }
            ),
            style = Typography.bodySmall,
            color = if (agreeType.isRequired) {
                ColorComponents.BottomSheet.Selected.ListItem.LabelRequired
            } else {
                ColorComponents.BottomSheet.UnSelected.ListItem.LabelOptional
            }
        )
    }
}