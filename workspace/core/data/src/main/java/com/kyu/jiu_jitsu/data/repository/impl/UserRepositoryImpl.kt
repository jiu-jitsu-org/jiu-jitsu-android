package com.kyu.jiu_jitsu.data.repository.impl

import com.kyu.jiu_jitsu.data.api.UserService
import com.kyu.jiu_jitsu.data.api.common.mapEnvelope
import com.kyu.jiu_jitsu.data.api.common.safeApiCall
import com.kyu.jiu_jitsu.data.api.common.toAppResult
import com.kyu.jiu_jitsu.data.model.dto.request.AppInfoRequest
import com.kyu.jiu_jitsu.data.model.dto.request.SignupRequest
import com.kyu.jiu_jitsu.data.model.dto.request.UpdateProfileRequest
import com.kyu.jiu_jitsu.data.model.dto.response.toInfo
import com.kyu.jiu_jitsu.data.repository.UserRepository
import com.kyu.jiu_jitsu.model.AppResult
import com.kyu.jiu_jitsu.model.LoginInfo
import com.kyu.jiu_jitsu.model.UserProfileInfo
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
): UserRepository {

    override suspend fun getUserProfileInfo(): AppResult<UserProfileInfo> =
        safeApiCall { userService.reqUserProfile() }
            .mapEnvelope { response -> response.toInfo() }

    override suspend fun updateUserProfileInfo(
        nickname: String,
        profileImageUrl: String,
    ): AppResult<UserProfileInfo> =
        safeApiCall {
            userService.updateUserProfile(
                UpdateProfileRequest(
                    nickname = nickname,
                    profileImageUrl = profileImageUrl,
                )
            )
        }.mapEnvelope { response -> response.toInfo() }

    override suspend fun signupUser(
        nickname: String,
        isMarketingAgreed: Boolean
    ): AppResult<LoginInfo> =
        safeApiCall {
            userService.signupUser(
                SignupRequest(
                    nickname = nickname,
                    isMarketingAgreed = isMarketingAgreed,
                )
            )
        }.mapEnvelope { response -> response.toInfo() }

    override suspend fun checkNickname(
        nickname: String
    ): AppResult<Boolean> =
        safeApiCall { userService.checkNickname(nickname) }
            .mapEnvelope { isAvailable -> isAvailable }

    override suspend fun appInfo(
        fcmToken: String,
        deviceId: String,
        osVersion: String
    ): AppResult<Boolean> =
        safeApiCall {
            userService.appInfo(
                AppInfoRequest(
                    fcmToken = fcmToken,
                    deviceId = deviceId,
                    osVersion = osVersion,
                )
            )
        }.toAppResult { success -> success }
}
