package com.kyu.jiu_jitsu.data.repository.impl

import com.kyu.jiu_jitsu.data.api.BootStrapService
import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.api.common.safeApiCall
import com.kyu.jiu_jitsu.data.model.dto.BootStrapResponse
import com.kyu.jiu_jitsu.data.repository.BootStrapRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class BootStrapInfoRepository @Inject constructor(
    private val bootStrapService: BootStrapService
) : BootStrapRepository {

    override suspend fun getBootStrapInfo(): Flow<ApiResult<BootStrapResponse>> = flow {
        emit(
            safeApiCall(block = {
                bootStrapService.reqBootstrapInfo()
            })
        )
    }.flowOn(Dispatchers.IO)

}