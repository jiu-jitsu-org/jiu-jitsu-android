package com.kyu.jiu_jitsu.domain.usecase.login

import com.kyu.jiu_jitsu.data.datastore.PrefKeys
import com.kyu.jiu_jitsu.data.datastore.SecurePreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalLoginTokenUseCase @Inject constructor(
    private val securePreferences: SecurePreferences
) {
    operator fun invoke(): Flow<String?> {
        return securePreferences.getValueToDecrypt(PrefKeys.USER_TOKEN)
    }
}