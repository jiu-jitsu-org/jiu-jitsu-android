package com.kyu.jiu_jitsu.profile.screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kyu.jiu_jitsu.data.model.GENDER_LIST
import com.kyu.jiu_jitsu.domain.yearsDescending
import com.kyu.jiu_jitsu.profile.ProfileViewModel
import com.kyu.jiu_jitsu.profile.R
import com.kyu.jiu_jitsu.profile.model.COMPETITION_LIST
import com.kyu.jiu_jitsu.ui.components.button.PrimaryButton
import com.kyu.jiu_jitsu.ui.components.picker.VerticalWheelPicker
import com.kyu.jiu_jitsu.ui.components.textfield.TransparentOutlinedTextField
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.White

sealed interface CompetitionScreenType {
    object TypeDate : CompetitionScreenType
    object TypeTitle : CompetitionScreenType
    object TypeResult : CompetitionScreenType
}

/**
 * 대회 정보 수정
 * @param modifier Modifier
 * @param padding PaddingValues
 * @param onCompleteClick 완료
 * @param onBackClick 뒤로가기
 */
@Composable
fun ModifyCompetitionScreen(
    modifier: Modifier,
    padding: PaddingValues,
    onCompleteClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel = hiltViewModel<ProfileViewModel>()

    /** Screen Index **/
    var screenIndex by remember { mutableStateOf<CompetitionScreenType>(CompetitionScreenType.TypeDate) }
    /** Selected Value **/
    var typeYearValueIndex by remember { mutableIntStateOf(0) }
    var typeMonthValueIndex by remember { mutableIntStateOf(0) }
    var typeTitleValue by remember { mutableStateOf("") }
    var typeResultValueIndex by remember { mutableIntStateOf(0) }
    /** Date Picker List **/
    var yearList: List<Int> by remember { mutableStateOf(listOf()) }
    var monthList: List<Int> by remember { mutableStateOf(listOf()) }

    // 상단 뒤로가기 클릭
    val onBackArrowClick: () -> Unit = {
        when(screenIndex) {
            CompetitionScreenType.TypeDate -> {
                onBackClick()
            }
            CompetitionScreenType.TypeTitle -> {
                screenIndex = CompetitionScreenType.TypeDate
            }
            CompetitionScreenType.TypeResult -> {
                screenIndex = CompetitionScreenType.TypeTitle
            }
        }
    }

    // 하단 버튼 클릭
    val onBottomBtnClick: () -> Unit = {
        when(screenIndex) {
            CompetitionScreenType.TypeDate -> {
                screenIndex = CompetitionScreenType.TypeTitle
            }
            CompetitionScreenType.TypeTitle -> {
                screenIndex = CompetitionScreenType.TypeResult
            }
            CompetitionScreenType.TypeResult -> {
                onCompleteClick()
            }
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = White,
    ) {
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            // 최상단 앱바
            Row(
                modifier = Modifier
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
                            onClick = onBackArrowClick
                        )
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.profile_competition_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = ColorComponents.Header.Header.Text,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(36.dp))
            }
            // 대회 정보 입력
            when(screenIndex) {
                CompetitionScreenType.TypeDate -> {
                    TypeDateLayout(
                        selectedYearIndex = typeYearValueIndex,
                        selectedMonthIndex = typeMonthValueIndex,
                    ) { yearIndex, monthIndex ->
                        typeYearValueIndex = yearIndex
                        typeMonthValueIndex = monthIndex
                        onBottomBtnClick()
                    }
                }
                CompetitionScreenType.TypeTitle -> {
                    TypeTitleLayout(
                        inputTitle = typeTitleValue,
                    ) { title ->
                        typeTitleValue = title
                        onBottomBtnClick()
                    }
                }
                CompetitionScreenType.TypeResult -> {
                    TypeResultLayout(
                        selectedResultIndex = typeResultValueIndex,
                    ) { resultIndex ->
                        typeResultValueIndex = resultIndex
                        onBottomBtnClick()
                    }
                }
            }
        }
    }
}

/**
 * 출전 정보 입력
 * @param selectedYearIndex 선택된 년도 인덱스
 * @param selectedMonthIndex 선택된 월 인덱스
 * @param onBottomBtnClick 하단 버튼 클릭
 */
@Composable
private fun TypeDateLayout(
    selectedYearIndex: Int = 0,
    selectedMonthIndex: Int = 0,
    onBottomBtnClick: (
        selectedYearIndex: Int,
        selectedMonthIndex: Int,
    ) -> Unit,
) {
    val yearList = yearsDescending(20)
    val monthList = (1..12).toList()

    var selectedYearIndex by remember { mutableIntStateOf(selectedYearIndex) }
    var selectedMonthIndex by remember { mutableIntStateOf(selectedMonthIndex) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.profile_competition_date_title),
            style = MaterialTheme.typography.titleSmall,
            color = ColorComponents.Header.Header.Text,
            textAlign = TextAlign.Center
        )
        // Date Picker
        Row (verticalAlignment = Alignment.CenterVertically) {
            // Year
            VerticalWheelPicker(
                items = yearList.map { it.toString() },
                initialIndex = selectedYearIndex,
                onSelected = { i, value -> selectedYearIndex = i },
                itemHeight = 44.dp,
                itemWidth  = 86.dp,
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Month
            VerticalWheelPicker(
                items = monthList.map { it.toString() },
                initialIndex = selectedMonthIndex,
                onSelected = { i, value -> selectedMonthIndex = i },
                itemHeight = 44.dp,
                itemWidth  = 86.dp
            )
        }
        // Bottom Button
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = "Bottom Button",
            onClick = { onBottomBtnClick(selectedYearIndex, selectedMonthIndex) }
        )
    }
}

/**
 * 대회명 입력
 * @param inputTitle 입력된 대회명
 * @param onBottomBtnClick 하단 버튼 클릭
 */
@Composable
private fun TypeTitleLayout(
    inputTitle: String = "",
    onBottomBtnClick: (
        inputTitle: String,
    ) -> Unit,
) {
    var strTitle by remember { mutableStateOf(inputTitle) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.profile_competition_name_title),
            style = MaterialTheme.typography.displayLarge,
            color = ColorComponents.TextFieldDisplay.Focus.Title,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        TransparentOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = strTitle,
            placeholder = "",
            errorTextColor = ColorComponents.TextFieldDisplay.Default.Placeholder,
            onValueChange = { new ->
                strTitle = new
            },
        )

        // Bottom Button
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = "Bottom Button",
            onClick = { onBottomBtnClick(strTitle) }
        )
    }
}

@Composable
private fun TypeResultLayout(
    selectedResultIndex: Int = 0,
    onBottomBtnClick: (
        selectedResultIndex: Int,
    ) -> Unit,
) {
    val resultList = COMPETITION_LIST
    var selectedIndex by remember { mutableIntStateOf(selectedResultIndex) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.profile_competition_result_title),
            style = MaterialTheme.typography.displayLarge,
            color = ColorComponents.TextFieldDisplay.Focus.Title,
            textAlign = TextAlign.Center
        )

        VerticalWheelPicker(
            items = resultList.map { it.displayName },
            initialIndex = selectedIndex,
            onSelected = { i, value -> selectedIndex = i },
            itemHeight = 44.dp,
            itemWidth  = 86.dp,
        )

        // Bottom Button
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = "Bottom Button",
            onClick = { onBottomBtnClick(selectedIndex) }
        )
    }
}