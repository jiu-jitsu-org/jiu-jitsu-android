package com.kyu.jiu_jitsu.data.api

import com.kyu.jiu_jitsu.data.model.RandomUserResponse
import com.kyu.jiu_jitsu.data.utils.NetworkConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserService {

    @GET(NetworkConfig.RandomUser.RESULTS)
    suspend fun reqRandomUser(
        @Query("results") results: Int
    ): RandomUserResponse
}