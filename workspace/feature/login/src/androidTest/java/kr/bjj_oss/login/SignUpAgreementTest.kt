package kr.bjj_oss.login

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import kr.bjj_oss.login.components.SignUpBottomSheet
import kr.bjj_oss.ui.theme.JiuJitsuPjtTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** 상세 열람은 동의가 아니며, 복원 후에도 필수/선택 동의 구분이 유지되어야 한다. */
class SignUpAgreementTest {
    @get:Rule val compose = createComposeRule()

    @Test fun serviceDetailsDoNotSelectAgreementAndRequiredChoicesSurviveRestoration() {
        var opened = 0
        var submitted: List<String>? = null
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            JiuJitsuPjtTheme {
                SignUpBottomSheet(onServiceTermsClick = { opened++ }) { submitted = it }
            }
        }
        compose.onNodeWithContentDescription("서비스 이용약관 동의 상세보기").performClick()
        compose.runOnIdle { assertEquals(1, opened) }
        compose.onNodeWithText("서비스 이용약관 동의").assertIsOff()
        listOf("서비스 이용약관 동의", "개인정보 처리방침 동의", "만 14세 이상입니다.").forEach {
            compose.onNodeWithText(it).performClick()
        }
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("서비스 이용약관 동의").assertIsOn()
        compose.onNodeWithText("마케팅 정보 수신").assertIsOff()
        compose.onNodeWithText("다음").performClick()
        compose.runOnIdle { assertEquals(setOf("Service", "Privacy", "Age"), submitted?.toSet()) }
    }
}
