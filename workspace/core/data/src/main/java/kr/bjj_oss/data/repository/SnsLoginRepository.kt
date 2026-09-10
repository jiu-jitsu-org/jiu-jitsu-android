package kr.bjj_oss.data.repository

import kr.bjj_oss.model.AppResult
import kr.bjj_oss.model.LoginInfo

/** Authentication boundary used by login presentation code. */
interface SnsLoginRepository {
    suspend fun login(
        snsProvider: String,
        token: String,
    ): AppResult<LoginInfo>
}
