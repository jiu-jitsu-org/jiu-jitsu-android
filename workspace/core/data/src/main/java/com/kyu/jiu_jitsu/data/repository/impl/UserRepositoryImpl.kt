package com.kyu.jiu_jitsu.data.repository.impl

import com.kyu.jiu_jitsu.data.api.UserService
import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.api.common.safeApiCall
import com.kyu.jiu_jitsu.data.model.dto.request.SignupRequest
import com.kyu.jiu_jitsu.data.model.dto.request.UpdateProfileRequest
import com.kyu.jiu_jitsu.data.model.dto.response.SnsLoginResponse
import com.kyu.jiu_jitsu.data.model.dto.response.UserProfileResponse
import com.kyu.jiu_jitsu.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
): UserRepository {

    override suspend fun getUserProfileInfo(): Flow<ApiResult<UserProfileResponse>> = flow {
        emit(
            safeApiCall {
                userService.reqUserProfile()
            }
        )
    }.flowOn(Dispatchers.IO)

    override suspend fun updateUserProfileInfo(
        nickname: String,
        profileImageUrl: String,
    ): Flow<ApiResult<UserProfileResponse>> = flow {
        emit(
            safeApiCall {
                userService.updateUserProfile(
                    UpdateProfileRequest(
                        nickname = nickname,
                        profileImageUrl = profileImageUrl,
                    )
                )
            }
        )
    }.flowOn(Dispatchers.IO)

    override suspend fun signupUser(
        nickname: String,
        isMarketingAgreed: Boolean
    ): Flow<ApiResult<SnsLoginResponse>> = flow {
        emit(
            safeApiCall {
                userService.signupUser(
                    SignupRequest(
                        nickname = nickname,
                        isMarketingAgreed = isMarketingAgreed,
                    )
                )
            }
        )
    }

}