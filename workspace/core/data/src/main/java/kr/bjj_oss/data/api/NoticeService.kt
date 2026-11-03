package kr.bjj_oss.data.api

import kr.bjj_oss.data.model.dto.request.UpdateNoticeSettingRequest
import kr.bjj_oss.data.model.dto.response.FetchNoticeSettingResponse
import kr.bjj_oss.data.model.dto.response.NoticeUnreadCountResponse
import kr.bjj_oss.data.utils.NetworkConfig
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

public interface NoticeService {

    @GET(NetworkConfig.Notice.NOTICE_UNREAD_COUNT)
    suspend fun reqNoticeUnreadCnt(): NoticeUnreadCountResponse

    @GET(NetworkConfig.Notice.NOTICE_SETTING)
    suspend fun fetchNoticeSetting(): FetchNoticeSettingResponse

    @POST(NetworkConfig.Notice.NOTICE_SETTING)
    suspend fun updateNoticeSetting(
        @Body body: UpdateNoticeSettingRequest,
    ): FetchNoticeSettingResponse

}