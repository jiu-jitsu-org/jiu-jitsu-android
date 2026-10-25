package kr.bjj_oss.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import kr.bjj_oss.BuildConfig
import kr.bjj_oss.model.AgreementPolicyType
import kr.bjj_oss.web.ServiceTermsContent

/** 별도 전체 화면 창을 사용하여 로그인 시트와 웹 로그인 부모의 상태/뒤로가기를 보존한다. */
@Composable
internal fun ServiceTermsDialog(type: AgreementPolicyType, onClose: () -> Unit) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        val view = LocalView.current
        // 시스템 바 대비는 app에서만 설정한다. 창이 닫히면 원래 화면의 창 설정이 그대로 적용된다.
        SideEffect {
            (view.parent as? DialogWindowProvider)?.window?.let { window ->
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = true
                    isAppearanceLightNavigationBars = true
                }
            }
        }
        Surface(Modifier.fillMaxSize(), color = Color.White) {
            // 상태바/카메라 컷아웃은 빈 흰 영역으로 남기고, 닫기 버튼은 제스처/탐색바 위에 둔다.
            Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                // BuildConfig는 app에서 주입하고, 목적지 분기는 웹 화면이 전달받은 타입으로 수행한다.
                ServiceTermsContent(
                    type = type,
                    origin = BuildConfig.OSS_WEB_ORIGIN,
                    privacyPolicyUrl = BuildConfig.PRIVACY_POLICY,
                    marketingPolicyUrl = BuildConfig.MARKETING_POLICY,
                    debuggingEnabled = BuildConfig.DEBUG,
                    onClose = onClose,
                )
            }
        }
    }
}
