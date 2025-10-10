package com.kyu.jiu_jitsu.data.repository

import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.model.dto.BootStrapResponse
import kotlinx.coroutines.flow.Flow

interface BootStrapRepository {
    suspend fun getBootStrapInfo(): Flow<ApiResult<BootStrapResponse>>
}