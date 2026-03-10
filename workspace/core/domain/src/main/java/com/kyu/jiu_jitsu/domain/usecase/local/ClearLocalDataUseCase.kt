package com.kyu.jiu_jitsu.domain.usecase.local

import com.kyu.jiu_jitsu.data.datastore.SecurePreferences
import javax.inject.Inject

class ClearLocalDataUseCase @Inject constructor(
    private val securePreferences: SecurePreferences
) {
    suspend operator fun invoke() {
        securePreferences.clear()
    }
}