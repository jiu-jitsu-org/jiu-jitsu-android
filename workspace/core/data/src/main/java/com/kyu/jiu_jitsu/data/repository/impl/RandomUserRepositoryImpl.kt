package com.kyu.jiu_jitsu.data.repository.impl

import com.kyu.jiu_jitsu.data.api.RandomUserService
import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.api.common.safeApiCall
import com.kyu.jiu_jitsu.data.model.RandomUserResponse
import com.kyu.jiu_jitsu.data.repository.RandomUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RandomUserRepositoryImpl @Inject constructor(
    private val randomUserService: RandomUserService
): RandomUserRepository {

    override suspend fun getRandomUser(results: Int): Flow<ApiResult<RandomUserResponse>> = flow {
        emit(safeApiCall(block = { randomUserService.reqRandomUser(results) }))
    }.flowOn(Dispatchers.IO)

}