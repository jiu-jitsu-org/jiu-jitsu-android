package kr.bjj_oss.data.api

import kr.bjj_oss.data.model.dto.response.BootStrapResponse
import kr.bjj_oss.data.utils.NetworkConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface BootStrapService {

    @GET(NetworkConfig.BootStrap.INFO)
    suspend fun reqBootstrapInfo(
        @Query("osName") osName: String = "ANDROID",
    ): BootStrapResponse

}