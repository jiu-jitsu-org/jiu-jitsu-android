package com.kyu.jiu_jitsu.data.api

import com.kyu.jiu_jitsu.data.model.dto.request.SignupRequest
import com.kyu.jiu_jitsu.data.model.dto.request.UpdateProfileRequest
import com.kyu.jiu_jitsu.data.model.dto.response.CheckNicknameResponse
import com.kyu.jiu_jitsu.data.model.dto.response.SnsLoginResponse
import com.kyu.jiu_jitsu.data.model.dto.response.UserProfileResponse
import com.kyu.jiu_jitsu.data.utils.NetworkConfig
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface UserService {

    // PROFILE
    @GET(NetworkConfig.UserController.USER_PROFILE)
    suspend fun reqUserProfile(): UserProfileResponse

    @PUT(NetworkConfig.UserController.USER_PROFILE)
    suspend fun updateUserProfile(
        @Body updateProfileRequest: UpdateProfileRequest
    ): UserProfileResponse

    // SIGN UP
    @POST(NetworkConfig.User.USER)
    suspend fun signupUser(
        @Body signupRequest: SignupRequest
    ): SnsLoginResponse

    // NICKNAME
    @GET(NetworkConfig.User.CHECK_NICKNAME)
    suspend fun checkNickname(
        @Query("nickname") nickname: String
    ): CheckNicknameResponse

}