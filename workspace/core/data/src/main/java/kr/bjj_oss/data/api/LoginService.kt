package kr.bjj_oss.data.api

import kr.bjj_oss.data.model.dto.request.SnsLoginRequest
import kr.bjj_oss.data.model.dto.response.SnsLoginResponse
import kr.bjj_oss.data.utils.NetworkConfig
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST(NetworkConfig.Authentication.SNS_LOGIN)
    suspend fun reqSnsLogin(
        @Body snsLoginRequest: SnsLoginRequest
    ): SnsLoginResponse
}