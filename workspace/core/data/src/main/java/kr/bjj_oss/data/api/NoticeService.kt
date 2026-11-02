package kr.bjj_oss.data.api

import kr.bjj_oss.data.model.dto.response.NoticeResponse
import kr.bjj_oss.data.utils.NetworkConfig
import retrofit2.http.GET

public interface NoticeService {

    @GET(NetworkConfig.Notice.NOTICE_UNREAD_COUNT)
    suspend fun reqNoticeUnreadCnt(): NoticeResponse

}