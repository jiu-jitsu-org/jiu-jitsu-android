package com.kyu.jiu_jitsu.data.repository.impl

import com.kyu.jiu_jitsu.data.api.BootStrapService
import com.kyu.jiu_jitsu.data.api.common.mapEnvelope
import com.kyu.jiu_jitsu.data.api.common.safeApiCall
import com.kyu.jiu_jitsu.data.model.dto.response.toInfo
import com.kyu.jiu_jitsu.data.repository.BootStrapRepository
import com.kyu.jiu_jitsu.model.AppResult
import com.kyu.jiu_jitsu.model.BootStrapInfo
import jakarta.inject.Inject

internal class BootStrapInfoRepository @Inject constructor(
    private val bootStrapService: BootStrapService
) : BootStrapRepository {

    override suspend fun getBootStrapInfo(): AppResult<BootStrapInfo> =
        safeApiCall { bootStrapService.reqBootstrapInfo() }
            .mapEnvelope { response -> response.toInfo() }
}
