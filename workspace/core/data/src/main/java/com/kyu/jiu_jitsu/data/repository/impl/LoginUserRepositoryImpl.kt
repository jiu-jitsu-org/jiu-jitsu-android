package com.kyu.jiu_jitsu.data.repository.impl

import com.kyu.jiu_jitsu.data.api.LoginService
import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.api.common.safeApiCall
import com.kyu.jiu_jitsu.data.model.SnsLoginRequest
import com.kyu.jiu_jitsu.data.model.SnsLoginResponse
import com.kyu.jiu_jitsu.data.repository.RefreshTokenRepository
import com.kyu.jiu_jitsu.data.repository.SnsLoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUserRepositoryImpl @Inject constructor(
    private val loginService: LoginService
) : SnsLoginRepository, RefreshTokenRepository {

    override suspend fun getSnsLoginUserInfo(
        snsProvider: String,
        token: String
    ): Flow<ApiResult<SnsLoginResponse>> = flow {
        val accessToken = if (snsProvider == "APPLE") null else token
        val idToken = if (snsProvider == "APPLE") token else null

        emit(
            safeApiCall(block = {
                loginService.reqSnsLogin(
                    SnsLoginRequest(
                        snsProvider = snsProvider,
                        accessToken = accessToken,
                        idToken = idToken,
                    )
                )
            })
        )

    }

    override suspend fun refreshToken() {
        print("refreshToken")
    }

}