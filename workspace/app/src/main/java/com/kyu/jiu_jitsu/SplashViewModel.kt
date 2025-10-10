package com.kyu.jiu_jitsu

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.data.datastore.SecurePreferences
import com.kyu.jiu_jitsu.data.model.BootStrapInfo
import com.kyu.jiu_jitsu.data.model.SplashModel
import com.kyu.jiu_jitsu.domain.usecase.GetBootStrapInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val getBootStrapInfoUseCase: GetBootStrapInfoUseCase,
): ViewModel() {

    var splashUiState by mutableStateOf<UiState<SplashModel>>(UiState.Idle)
    var splashModel by mutableStateOf(SplashModel())

    fun startLogic() {
        viewModelScope.launch {
            splashUiState = UiState.Loading

            joinAll(
                getBootStrapInfo(),
                checkAutoLogin()
            )

            if (splashModel.bootStrapInfo != null) {
                splashUiState = UiState.Success(splashModel)
            } else {
                // TODO BootStrap API Error message
                splashUiState = UiState.Error("", false)
            }
        }
    }

    fun isNeedAppUpdate(): Boolean {

        return false
    }

    // check App Version
    private fun getBootStrapInfo(): Job =
        viewModelScope.launch {
            getBootStrapInfoUseCase().collectLatest { uiState ->
                when(uiState) {
                    is UiState.Success -> {
                        Log.d("@@@@@@@@@@@", "SplashViewModel getBootStrapInfo Success : ${uiState.result}")
                        splashModel.bootStrapInfo = uiState.result

                    }
                    is UiState.Error -> {
                        Log.d("@@@@@@@@@@@", "SplashViewModel getBootStrapInfo Error : ${uiState.message}")
                    }
                    else -> {
                        Log.d("@@@@@@@@@", "SplashViewModel getBootStrapInfo else")
                    }
                }
            }
        }

    // TODO : 자동 로그인 여부 체크
    private fun checkAutoLogin(): Job =
        viewModelScope.launch {
            delay(1000L)
            splashModel.autoLogin = true
        }

}