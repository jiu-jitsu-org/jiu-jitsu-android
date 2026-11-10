package kr.bjj_oss.data.api

import kr.bjj_oss.data.model.dto.request.UpdateCommunityProfileRequest
import kr.bjj_oss.data.model.dto.response.CommunityProfileData
import kr.bjj_oss.data.model.dto.response.CommunityProfileResponse
import kr.bjj_oss.data.utils.NetworkConfig
import com.squareup.moshi.JsonClass
import kr.bjj_oss.data.model.dto.response.ApiResponse
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CommunityService {

    @GET("api/community/competitions")
    suspend fun searchCompetitions(@Query("keyword") keyword: String): CompetitionSuggestionsResponse

    // 내 커뮤니티 프로필 조회
    @GET(NetworkConfig.CommunityProfileController.COMMUNITY_PROFILE)
    suspend fun reqCommunityProfile(): CommunityProfileResponse

    // 내 커뮤니티 프로필 수정
    @POST(NetworkConfig.CommunityProfileController.COMMUNITY_PROFILE)
    suspend fun modifyCommunityProfile(
        @Body body: UpdateCommunityProfileRequest
    ): CommunityProfileResponse

}

@JsonClass(generateAdapter = true)
data class CompetitionSuggestionsResponse(
    override val success: Boolean?, override val code: String?, override val message: String?,
    override val data: List<CompetitionSuggestionDto>?,
) : ApiResponse<List<CompetitionSuggestionDto>>

@JsonClass(generateAdapter = true)
data class CompetitionSuggestionDto(val competitionId: Int, val name: String, val year: Int? = null)
