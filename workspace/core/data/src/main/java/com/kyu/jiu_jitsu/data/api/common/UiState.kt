package com.kyu.jiu_jitsu.data.api.common

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val result: T) : UiState<T>
    data class Error(val message: String, val retryable: Boolean) : UiState<Nothing>
}