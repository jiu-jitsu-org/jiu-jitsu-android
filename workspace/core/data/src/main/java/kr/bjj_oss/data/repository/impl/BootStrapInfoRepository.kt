package kr.bjj_oss.data.repository.impl

import kr.bjj_oss.data.api.BootStrapService
import kr.bjj_oss.data.api.common.mapEnvelope
import kr.bjj_oss.data.api.common.safeApiCall
import kr.bjj_oss.data.model.dto.response.toInfo
import kr.bjj_oss.data.repository.BootStrapRepository
import kr.bjj_oss.model.AppResult
import kr.bjj_oss.model.BootStrapInfo
import jakarta.inject.Inject

internal class BootStrapInfoRepository @Inject constructor(
    private val bootStrapService: BootStrapService
) : BootStrapRepository {

    override suspend fun getBootStrapInfo(): AppResult<BootStrapInfo> =
        safeApiCall { bootStrapService.reqBootstrapInfo() }
            .mapEnvelope { response -> response.toInfo() }
}
