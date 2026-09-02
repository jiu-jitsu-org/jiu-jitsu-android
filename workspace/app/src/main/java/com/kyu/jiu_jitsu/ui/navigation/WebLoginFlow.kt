package com.kyu.jiu_jitsu.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kyu.jiu_jitsu.login.screen.LoginScreen
import com.kyu.jiu_jitsu.nickname.screen.NickNameScreen
import com.kyu.jiu_jitsu.ui.routes.LoginScreen as LoginDestination
import com.kyu.jiu_jitsu.ui.routes.NickNameScreen as NicknameDestination

/** App composes the two native features while feature:web retains the requesting document. */
@Composable
internal fun WebLoginFlow(padding: PaddingValues, onFinished: (Boolean) -> Unit) {
    val nav = rememberNavController()
    NavHost(nav, startDestination = LoginDestination, modifier = Modifier.fillMaxSize().padding(padding)) {
        composable<LoginDestination> {
            LoginScreen(Modifier.fillMaxSize(), goHome = { onFinished(true) }, onSkip = { onFinished(false) }, goInputNickName = {
                nav.navigate(NicknameDestination(it))
            })
        }
        composable<NicknameDestination> { entry ->
            NickNameScreen(
                modifier = Modifier.fillMaxSize(), padding = PaddingValues(0.dp),
                isMarketingAgree = entry.toRoute<NicknameDestination>().isMarketingAgreed,
                goHome = { onFinished(true) },
            )
        }
    }
    BackHandler { onFinished(false) }
}
