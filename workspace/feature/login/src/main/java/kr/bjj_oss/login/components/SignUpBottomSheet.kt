package kr.bjj_oss.login.components

import androidx.annotation.StringRes
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kr.bjj_oss.login.R
import kr.bjj_oss.model.AgreementPolicyType
import kr.bjj_oss.ui.components.button.PrimaryCTAButton
import kr.bjj_oss.ui.theme.ColorComponents
import kr.bjj_oss.ui.theme.Typography

sealed class SignUpAgreeType(
    @StringRes val titleResId: Int,
    val isRequired: Boolean = true,
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
    onServiceTermsClick: (type: AgreementPolicyType) -> Unit,
    agreeInfoSubmit: (agreeList: List<String>) -> Unit,
) {
    val listState = rememberLazyListState()

    val agreementTypes = remember {
        listOf(SignUpAgreeType.Service, SignUpAgreeType.Privacy, SignUpAgreeType.Age, SignUpAgreeType.Marketing)
    }
    // 코드 목록만 저장하여 약관을 열거나 기기를 회전해도 체크 상태를 복원한다.
    var selectedCodes by rememberSaveable { mutableStateOf(arrayListOf<String>()) }
    val isPossibleSubmit = agreementTypes.filter { it.isRequired }.all { it.code in selectedCodes }

    Column(
        modifier = Modifier.padding(
            horizontal = 20.dp,
            vertical = 15.dp,
        ),
    ) {
        // Title
        Text(
            stringResource(R.string.signup_bottom_sheet_title),
            style = Typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Agree List
        LazyColumn(
            modifier = Modifier.weight(1f, fill = false),
            state = listState,
        ) {
            items(
                items = agreementTypes.sortedBy { it.index },
                key = { it.code },
            ) { keyItem ->
                // List Item
                SignUpAgreeItem(
                    agreeType = keyItem,
                    isSelected = keyItem.code in selectedCodes,
                    onItemClick = { item ->
                        selectedCodes = ArrayList(if (item.code in selectedCodes)
                            selectedCodes - item.code else selectedCodes + item.code)
                    },
                    onInfoMoreClick = { item ->
                        when(item) {
                            // 상세보기와 동의 선택은 별개 이벤트다. 열기만으로 동의하지 않는다.
                            SignUpAgreeType.Service -> onServiceTermsClick(AgreementPolicyType.Service)
                            SignUpAgreeType.Privacy -> onServiceTermsClick(AgreementPolicyType.Privacy)
                            SignUpAgreeType.Age -> {}
                            SignUpAgreeType.Marketing -> onServiceTermsClick(AgreementPolicyType.Marketing)
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Agree Submit CTA Button
        PrimaryCTAButton(
            modifier = Modifier
                .heightIn(min = 51.dp)
                .fillMaxWidth(),
            text = if (isPossibleSubmit) {
                stringResource(kr.bjj_oss.ui.R.string.common_next)
            } else {
                stringResource(R.string.signup_agree_all)
            },
            onClick = {
                if (isPossibleSubmit) {
                    agreeInfoSubmit(selectedCodes.toList())
                } else {
                    selectedCodes = ArrayList(agreementTypes.map { it.code })
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
    onInfoMoreClick: (SignUpAgreeType) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .heightIn(min = 48.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 48.dp)
                .toggleable(
                    value = isSelected,
                    role = Role.Checkbox,
                    onValueChange = { onItemClick(agreeType) },
                )
                .padding(end = 20.dp)
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Check Icon
            Icon(
                painter = painterResource(kr.bjj_oss.ui.R.drawable.ic_check),
                contentDescription = null,
                tint = if (isSelected) {
                    ColorComponents.BottomSheet.Selected.ListItem.LeadingIcon
                } else {
                    ColorComponents.BottomSheet.UnSelected.ListItem.LeadingIcon
                }
            )
            Spacer(modifier = Modifier.width(9.dp))
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                Spacer(modifier = Modifier.width(9.dp))
                // Required or Optional
                Text(
                    stringResource(
                        if (agreeType.isRequired) {
                            kr.bjj_oss.ui.R.string.common_required
                        } else {
                            kr.bjj_oss.ui.R.string.common_optional
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
        // 상세보기 버튼은 아이콘 크기를 유지하면서 48dp 터치 영역을 제공한다.
        IconButton(onClick = { onInfoMoreClick(agreeType) }) {
            Icon(
                painter = painterResource(kr.bjj_oss.ui.R.drawable.ic_arow_right),
                contentDescription = stringResource(R.string.signup_agreement_details, stringResource(agreeType.titleResId)),
                tint = ColorComponents.BottomSheet.Selected.ListItem.FollowingIcon,
            )
        }
    }
}
