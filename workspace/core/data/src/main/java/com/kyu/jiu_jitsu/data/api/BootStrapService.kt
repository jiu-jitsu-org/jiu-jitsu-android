package com.kyu.jiu_jitsu.data.api

import com.kyu.jiu_jitsu.data.model.dto.response.BootStrapResponse
import com.kyu.jiu_jitsu.data.utils.NetworkConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface BootStrapService {

    @GET(NetworkConfig.BootStrap.INFO)
    suspend fun reqBootstrapInfo(
        @Query("osName") osName: String = "ANDROID",
    ): BootStrapResponse

}