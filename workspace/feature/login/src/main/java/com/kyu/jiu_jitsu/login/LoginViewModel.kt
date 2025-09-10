package com.kyu.jiu_jitsu.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyu.jiu_jitsu.data.api.common.UiState
import com.kyu.jiu_jitsu.domain.usecase.GetRandomUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getRandomUserUseCase: GetRandomUserUseCase
): ViewModel() {

    fun getUser(results: Int = 20) {
        viewModelScope.launch {
            getRandomUserUseCase(results).collect { uiState ->
                when(uiState) {
                    is UiState.Success -> {
                        Log.d("LoginViewModel", "getUser Success : ${uiState.result}")
                    }
                    is UiState.Error -> {
                        Log.d("LoginViewModel", "getUser Error : ${uiState.message}")
                    }
                    is UiState.Idle -> {
                        Log.d("LoginViewModel", "getUser Idle")
                    }
                    is UiState.Loading -> {
                        Log.d("LoginViewModel", "getUser Loading")
                    }
                }
            }
        }
    }

}