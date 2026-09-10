package kr.bjj_oss.data.repository

import kr.bjj_oss.model.AppResult
import kr.bjj_oss.model.BootStrapInfo

/** Source of the configuration required to make the application's startup decision. */
interface BootStrapRepository {
    suspend fun getBootStrapInfo(): AppResult<BootStrapInfo>
}
