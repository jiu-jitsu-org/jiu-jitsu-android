package kr.bjj_oss.data.repository.impl

import kr.bjj_oss.data.api.NoticeService
import kr.bjj_oss.data.api.common.mapEnvelope
import kr.bjj_oss.data.api.common.safeApiCall
import kr.bjj_oss.data.model.dto.request.UpdateNoticeSettingRequest
import kr.bjj_oss.data.model.dto.response.toInfo
import kr.bjj_oss.data.repository.NoticeRepository
import kr.bjj_oss.model.AppResult
import kr.bjj_oss.model.NoticeSettingInfo
import kr.bjj_oss.model.NoticeUnreadCountInfo
import javax.inject.Inject

class NoticeRepositoryImpl @Inject constructor(
    private val noticeService: NoticeService
): NoticeRepository {

    override suspend fun getNoticeUnreadCnt(): AppResult<Int?> =
        safeApiCall { noticeService.reqNoticeUnreadCnt() }
            .mapEnvelope { response -> response }

    override suspend fun fetchNoticeSetting(): AppResult<NoticeSettingInfo> =
        safeApiCall { noticeService.fetchNoticeSetting() }
            .mapEnvelope { response -> response.toInfo() }

    override suspend fun updateNoticeSetting(body: UpdateNoticeSettingRequest): AppResult<NoticeSettingInfo> =
        safeApiCall { noticeService.updateNoticeSetting(body) }
            .mapEnvelope { response -> response.toInfo() }

}