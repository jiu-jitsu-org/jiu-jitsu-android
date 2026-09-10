package kr.bjj_oss.data.repository.impl

import kr.bjj_oss.data.api.LoginService
import kr.bjj_oss.data.api.common.mapEnvelope
import kr.bjj_oss.data.api.common.safeApiCall
import kr.bjj_oss.data.model.dto.request.SnsLoginRequest
import kr.bjj_oss.data.model.dto.response.toInfo
import kr.bjj_oss.data.repository.SnsLoginRepository
import kr.bjj_oss.model.AppResult
import kr.bjj_oss.model.LoginInfo
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
