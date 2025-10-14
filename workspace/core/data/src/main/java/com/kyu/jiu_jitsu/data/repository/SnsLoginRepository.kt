package com.kyu.jiu_jitsu.data.repository

import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.model.dto.response.SnsLoginResponse
import kotlinx.coroutines.flow.Flow

interface SnsLoginRepository {
    suspend fun getSnsLoginUserInfo(
        snsProvider: String,
        token: String)
    : Flow<ApiResult<SnsLoginResponse>>

}