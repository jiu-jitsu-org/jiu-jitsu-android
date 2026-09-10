package kr.bjj_oss.data.api

import kr.bjj_oss.data.model.dto.request.UpdateCommunityProfileRequest
import kr.bjj_oss.data.model.dto.response.CommunityProfileData
import kr.bjj_oss.data.model.dto.response.CommunityProfileResponse
import kr.bjj_oss.data.utils.NetworkConfig
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CommunityService {

    // 내 커뮤니티 프로필 조회
    @GET(NetworkConfig.CommunityProfileController.COMMUNITY_PROFILE)
    suspend fun reqCommunityProfile(): CommunityProfileResponse

    // 내 커뮤니티 프로필 수정
    @POST(NetworkConfig.CommunityProfileController.COMMUNITY_PROFILE)
    suspend fun modifyCommunityProfile(
        @Body body: UpdateCommunityProfileRequest
    ): CommunityProfileResponse

}