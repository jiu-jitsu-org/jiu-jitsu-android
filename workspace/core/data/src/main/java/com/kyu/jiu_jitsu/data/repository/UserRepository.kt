package com.kyu.jiu_jitsu.data.repository

import com.kyu.jiu_jitsu.model.AppResult
import com.kyu.jiu_jitsu.model.LoginInfo
import com.kyu.jiu_jitsu.model.UserProfileInfo

/** Account operations exposed as app models, never Retrofit response types. */
interface UserRepository {
    suspend fun getUserProfileInfo(): AppResult<UserProfileInfo>

    suspend fun updateUserProfileInfo(
        nickname: String,
        profileImageUrl: String,
    ): AppResult<UserProfileInfo>

    suspend fun signupUser(
        nickname: String,
        isMarketingAgreed: Boolean,
    ): AppResult<LoginInfo>

    suspend fun checkNickname(
        nickname: String,
    ): AppResult<Boolean>

    suspend fun appInfo(
        fcmToken: String,
        deviceId: String,
        osVersion: String
    ): AppResult<Boolean>
}
