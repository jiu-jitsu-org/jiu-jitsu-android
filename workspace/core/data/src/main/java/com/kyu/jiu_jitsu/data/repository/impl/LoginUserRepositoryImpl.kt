package com.kyu.jiu_jitsu.data.repository.impl

import com.kyu.jiu_jitsu.data.api.LoginService
import com.kyu.jiu_jitsu.data.api.common.mapEnvelope
import com.kyu.jiu_jitsu.data.api.common.safeApiCall
import com.kyu.jiu_jitsu.data.model.dto.request.SnsLoginRequest
import com.kyu.jiu_jitsu.data.model.dto.response.toInfo
import com.kyu.jiu_jitsu.data.repository.SnsLoginRepository
import com.kyu.jiu_jitsu.model.AppResult
import com.kyu.jiu_jitsu.model.LoginInfo
import javax.inject.Inject

internal class LoginUserRepositoryImpl @Inject constructor(
    private val loginService: LoginService
) : SnsLoginRepository {

    override suspend fun login(
        snsProvider: String,
        token: String
    ): AppResult<LoginInfo> {
        // Apple returns an ID token, whereas the other providers use an OAuth access token.
        // The distinction is a backend transport detail and therefore stays in this implementation.
        val accessToken = if (snsProvider == "APPLE") null else token
        val idToken = if (snsProvider == "APPLE") token else null

        return safeApiCall {
            loginService.reqSnsLogin(
                SnsLoginRequest(
                    snsProvider = snsProvider,
                    accessToken = accessToken,
                    idToken = idToken,
                )
            )
        }.mapEnvelope { response -> response.toInfo() }
    }
}
