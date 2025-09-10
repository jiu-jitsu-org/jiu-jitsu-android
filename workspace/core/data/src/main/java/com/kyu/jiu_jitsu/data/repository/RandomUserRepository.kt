package com.kyu.jiu_jitsu.data.repository

import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.model.RandomUserResponse
import kotlinx.coroutines.flow.Flow

interface RandomUserRepository {
    suspend fun getRandomUser(results: Int): Flow<ApiResult<RandomUserResponse>>
}