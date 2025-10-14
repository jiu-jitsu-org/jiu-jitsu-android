package com.kyu.jiu_jitsu.domain.usecase.login

import com.kyu.jiu_jitsu.data.datastore.PrefKeys
import com.kyu.jiu_jitsu.data.datastore.SecurePreferences
import com.kyu.jiu_jitsu.data.module.NetworkModule.setUserToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CheckAutoLoginUseCase @Inject constructor(
    private val securePreferences: SecurePreferences
) {
    operator fun invoke(): Flow<Boolean> {
        return securePreferences.getValueToDecrypt(PrefKeys.USER_TOKEN)
            .map { token ->
                token?.setUserToken()
                token != null
            }
    }

}