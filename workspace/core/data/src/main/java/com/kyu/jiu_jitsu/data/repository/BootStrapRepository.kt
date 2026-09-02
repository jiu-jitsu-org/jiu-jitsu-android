package com.kyu.jiu_jitsu.data.repository

import com.kyu.jiu_jitsu.model.AppResult
import com.kyu.jiu_jitsu.model.BootStrapInfo

/** Source of the configuration required to make the application's startup decision. */
interface BootStrapRepository {
    suspend fun getBootStrapInfo(): AppResult<BootStrapInfo>
}
