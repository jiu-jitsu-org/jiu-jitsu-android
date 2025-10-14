package com.kyu.jiu_jitsu.domain.usecase.user

import com.kyu.jiu_jitsu.data.datastore.PrefKeys
import com.kyu.jiu_jitsu.data.datastore.SecurePreferences
import com.kyu.jiu_jitsu.data.module.NetworkModule.setUserToken
import javax.inject.Inject

class SaveLocalUserInfoUseCase @Inject constructor(
    private val securePreferences: SecurePreferences
) {
    suspend operator fun invoke(
        token: String? = null,
        nickName: String? = null,
        userProfileImg: String? = null,
    ) {
        token?.let {
            securePreferences.setValueToEncrypt(PrefKeys.USER_TOKEN, it)
            it.setUserToken()
        }

        nickName?.let {
            securePreferences.setValueToEncrypt(PrefKeys.USER_NICK_NAME, it)
        }

        userProfileImg?.let {
            securePreferences.setValueToEncrypt(PrefKeys.USER_PROFILE_IMG, it)
        }
    }
}