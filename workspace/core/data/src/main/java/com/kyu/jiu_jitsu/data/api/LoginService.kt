package com.kyu.jiu_jitsu.data.api

import com.kyu.jiu_jitsu.data.model.SnsLoginRequest
import com.kyu.jiu_jitsu.data.model.SnsLoginResponse
import com.kyu.jiu_jitsu.data.utils.NetworkConfig
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST(NetworkConfig.Authentication.SNS_LOGIN)
    suspend fun reqSnsLogin(
        @Body snsLoginRequest: SnsLoginRequest
    ): SnsLoginResponse
}