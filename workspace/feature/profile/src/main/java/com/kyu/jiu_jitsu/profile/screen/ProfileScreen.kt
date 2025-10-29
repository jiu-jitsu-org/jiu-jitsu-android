package com.kyu.jiu_jitsu.profile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.profile.ProfileViewModel
import com.kyu.jiu_jitsu.ui.R
import com.kyu.jiu_jitsu.ui.components.button.TextButton
import com.kyu.jiu_jitsu.ui.theme.Blue500
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.CoolGray25
import com.kyu.jiu_jitsu.ui.theme.CoolGray500
import com.kyu.jiu_jitsu.ui.theme.CoolGray75
import com.kyu.jiu_jitsu.ui.theme.Red500
import com.kyu.jiu_jitsu.ui.theme.White

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onModifyClick: () -> Unit = {},
) {
    val viewModel = hiltViewModel<ProfileViewModel>()
    /** 프로필 UI 상태 (선택한 데이터가 있는지) */
    var defaultUiFlag by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // TODO fetch User Profile data
    }

    LaunchedEffect(viewModel.userProfileState) {
        val uiState = viewModel.userProfileState
        when(uiState) {
            is UiState.Success -> {
                // TODO Success
            }
            is UiState.Error -> {
                // TODO Error
            }
            is UiState.Loading -> {
                // TODO Loading
            }
            else -> {}
        }
    }

    Surface(
        modifier = modifier,
        color = if (defaultUiFlag) CoolGray500 else Blue500,
    ) {
        LazyColumn(
            modifier = Modifier
                .safeDrawingPadding()
                .fillMaxSize(),
        ) {
            /** 상단 TopBar */
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        text = stringResource(R.string.common_modify),
                        onClick = onModifyClick,
                    )
                }
                Spacer(modifier = Modifier.height(27.dp))
            }
            /** 유저 프로필 + 닉네임 */
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(
                                color = White,
                                shape = RoundedCornerShape(15.dp),
                            ),
                    )
                    Text(
                        text = "닉네임",
                        style = MaterialTheme.typography.titleMedium,
                        color = CoolGray75
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                }
            }
            /** 벨트, 체급 */
            item {
                ConstraintLayout(
                    modifier = Modifier
                        .height(128.dp)
                        .fillMaxWidth()
                ) {
                    val (infoRow, bg) = createRefs()

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(
                                color = Red500,
                            )
                            .constrainAs(bg) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                            }
                    )

                    Row(
                        modifier = Modifier
                            .height(128.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .background(
                                color = ColorComponents.List.Setting.Background,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .constrainAs(infoRow) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    ) {
                        /** 벨트 */

                        /** 체급 */
                    }
                }

            }
        }
    }
}