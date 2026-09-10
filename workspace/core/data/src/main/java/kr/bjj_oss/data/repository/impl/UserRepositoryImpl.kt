package kr.bjj_oss.data.repository.impl

import kr.bjj_oss.data.api.UserService
import kr.bjj_oss.data.api.common.mapEnvelope
import kr.bjj_oss.data.api.common.safeApiCall
import kr.bjj_oss.data.api.common.toAppResult
import kr.bjj_oss.data.model.dto.request.AppInfoRequest
import kr.bjj_oss.data.model.dto.request.SignupRequest
import kr.bjj_oss.data.model.dto.request.UpdateProfileRequest
import kr.bjj_oss.data.model.dto.response.toInfo
import kr.bjj_oss.data.repository.UserRepository
import kr.bjj_oss.model.AppResult
import kr.bjj_oss.model.LoginInfo
import kr.bjj_oss.model.UserProfileInfo
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
