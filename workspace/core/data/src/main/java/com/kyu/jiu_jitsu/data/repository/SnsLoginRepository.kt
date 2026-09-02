package com.kyu.jiu_jitsu.data.repository

import com.kyu.jiu_jitsu.model.AppResult
import com.kyu.jiu_jitsu.model.LoginInfo

/** Authentication boundary used by login presentation code. */
interface SnsLoginRepository {
    suspend fun login(
        snsProvider: String,
        token: String,
    ): AppResult<LoginInfo>
}
