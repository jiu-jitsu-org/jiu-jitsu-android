package kr.bjj_oss.login.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.bjj_oss.login.LoginViewModel
import kr.bjj_oss.login.R
import kr.bjj_oss.model.AgreementPolicyType
import kr.bjj_oss.login.components.SignUpBottomSheet
import kr.bjj_oss.login.components.SignUpAgreeType
import kr.bjj_oss.login.model.LoginType
import kr.bjj_oss.login.model.SnsLoginSucceedType
import kr.bjj_oss.ui.belt.BeltLogin
import kr.bjj_oss.ui.components.button.PressableButton
import kr.bjj_oss.ui.components.button.PressableTextButton
import kr.bjj_oss.ui.state.UiState
import kr.bjj_oss.ui.theme.AppleBg
import kr.bjj_oss.ui.theme.Color1F1F1F
import kr.bjj_oss.ui.theme.ColorComponents
import kr.bjj_oss.ui.theme.CoolGray75
import kr.bjj_oss.ui.theme.KakaoBg
import kr.bjj_oss.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier,
    goHome: () -> Unit,
    goInputNickName: (isMarketingAgreed: Boolean) -> Unit,
    serviceTermsContent: @Composable (type: AgreementPolicyType, onClose: () -> Unit) -> Unit,
    onSkip: () -> Unit = goHome,
    showBeltCrowd: Boolean = false,
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<LoginViewModel>()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var open by rememberSaveable { mutableStateOf(false) }
    // 시트 열림 여부와 열어 본 약관 종류를 저장해 화면 재생성 시 표시 상태를 복원한다.
    // 약관을 읽을 때는 시트를 composition에 유지해 동의 선택이 그대로 남게 한다.
    var serviceTermsType by rememberSaveable { mutableStateOf<AgreementPolicyType?>(null) }

    LaunchedEffect(viewModel.loginUiState) {
        if (viewModel.loginUiState is UiState.Error) open = false
        // 로그인 성공은 가입 시트 열기 또는 홈 이동으로 한 번 처리한다.
        when (viewModel.consumeLoginSuccess()) {
            SnsLoginSucceedType.SIGN_UP -> open = true
            SnsLoginSucceedType.SIGN_IN -> {
                open = false
                goHome()
            }
            null -> Unit
        }
    }

    // 독립 화면과 웹 로그인은 버튼 동작과 스크롤 상태를 공유하고 배치와 대비만 각 화면에 맞춘다.
    // 버튼은 높이 하한을 두고 내용에 따라 늘어날 수 있게 한다. 시트보다 높아지면 스크롤한다.
    val scrollState = rememberScrollState()
    val buttons: @Composable (Modifier, Dp, Dp, Dp) -> Unit = {
            contentModifier, horizontalPadding, topPadding, bottomPadding ->
        val googleBackground = if (showBeltCrowd) CoolGray75 else White
        val skipColor = if (showBeltCrowd) Color1F1F1F else White
        Column(
            modifier = contentModifier
                .verticalScroll(scrollState)
                .padding(start = horizontalPadding, end = horizontalPadding, top = topPadding, bottom = bottomPadding),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Kakao Test
            PressableButton(
                modifier = Modifier
                    .heightIn(min = 52.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.login_kakao_test),
                disabledBgColor = KakaoBg,
                enabledBgColor = KakaoBg,
                pressedBgColor = KakaoBg,
                enableTextColor = Color1F1F1F,
                pressedTextColor = Color1F1F1F,
                disabledTextColor = Color1F1F1F,
                roundedCorner = 12.dp,
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_login_kakao),
                        contentDescription = null
                    )
                },
                onClick = {
                    viewModel.loginType = LoginType.KAKAO_ACCOUNT
                    viewModel.startKakaoTestLogin()
                },
            )
            // Kakao
            PressableButton(
                modifier = Modifier
                    .heightIn(min = 52.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.login_kakao),
                disabledBgColor = KakaoBg,
                enabledBgColor = KakaoBg,
                pressedBgColor = KakaoBg,
                enableTextColor = Color1F1F1F,
                pressedTextColor = Color1F1F1F,
                disabledTextColor = Color1F1F1F,
                roundedCorner = 12.dp,
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_login_kakao),
                        contentDescription = null
                    )
                },
                onClick = {
                    viewModel.loginType = LoginType.KAKAO_ACCOUNT
                    viewModel.startSnsLogin(context)
                },
            )
            Spacer(modifier = Modifier.height(15.dp))
            // Google
            PressableButton(
                modifier = Modifier
                    .heightIn(min = 52.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.login_google),
                disabledBgColor = googleBackground,
                enabledBgColor = googleBackground,
                pressedBgColor = googleBackground,
                enableTextColor = Color1F1F1F,
                pressedTextColor = Color1F1F1F,
                disabledTextColor = Color1F1F1F,
                roundedCorner = 12.dp,
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_login_google),
                        contentDescription = null
                    )
                },
                onClick = {
                    viewModel.loginType = LoginType.GOOGLE
                },
            )
            Spacer(modifier = Modifier.height(15.dp))
            // Apple
            PressableButton(
                modifier = Modifier
                    .heightIn(min = 52.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.login_apple),
                disabledBgColor = AppleBg,
                enabledBgColor = AppleBg,
                pressedBgColor = AppleBg,
                enableTextColor = White,
                pressedTextColor = White,
                disabledTextColor = White,
                roundedCorner = 12.dp,
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_login_apple),
                        contentDescription = null
                    )
                },
                onClick = {
                    viewModel.loginType = LoginType.APPLE
                },
            )
            Spacer(modifier = Modifier.height(15.dp))
            // Skip
            PressableTextButton(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.login_skip),
                pressedTextColor = skipColor,
                enableTextColor = skipColor,
                disabledTextColor = skipColor,
                onClick = onSkip
            )
        }
    }
    if (showBeltCrowd) {
        // 장면이 측정한 하단 여백을 시트 안에 적용한다. 인증 상태와 약관 표시는 로그인 화면이 맡는다.
        BeltLogin(modifier) { bottomPadding ->
            buttons(Modifier.fillMaxWidth(), 35.dp, 28.dp, bottomPadding)
        }
    } else {
        // 웹 로그인은 기존 컨테이너에서 버튼의 최대 폭과 시스템 바 여백을 정한다.
        Surface(color = CoolGray75, modifier = modifier) {
            buttons(
                Modifier.fillMaxHeight()
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .widthIn(max = 560.dp)
                    .fillMaxWidth(),
                35.dp, 35.dp, 50.dp,
            )
        }
    }

    if (open) {
        ModalBottomSheet(
            containerColor = ColorComponents.BottomSheet.Selected.Container.Background,
            contentColor = ColorComponents.BottomSheet.Selected.Container.Title,
            scrimColor = ColorComponents.BottomSheet.Selected.Container.Scrim,
            sheetState = sheetState,
            onDismissRequest = {
                serviceTermsType = null
                open = false
            },
            shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    width = 48.dp,
                    height = 4.dp,
                    color = ColorComponents.BottomSheet.Selected.Container.Handle
                )
            },
        ) {
            SignUpBottomSheet(onServiceTermsClick = { type -> serviceTermsType = type }) { agreeList ->
                serviceTermsType = null
                open = false
                // Marketing is optional. Derive the backend flag from the actual selection rather
                // than treating submission of the required agreements as marketing consent.
                goInputNickName(agreeList.contains(SignUpAgreeType.Marketing.code))
            }
        }
    }

    val selectedPolicy = serviceTermsType
    if (open && selectedPolicy != null) {
        // 선택한 약관 종류를 app slot으로 전달하고, 닫을 때만 선택 목적지를 초기화한다.
        serviceTermsContent(selectedPolicy) { serviceTermsType = null }
    }

}
