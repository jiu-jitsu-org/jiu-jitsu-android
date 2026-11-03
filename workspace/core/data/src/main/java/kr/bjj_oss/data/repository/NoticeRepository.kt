package kr.bjj_oss.data.repository

import kr.bjj_oss.data.model.dto.request.UpdateNoticeSettingRequest
import kr.bjj_oss.model.AppResult
import kr.bjj_oss.model.NoticeSettingInfo

interface NoticeRepository {
    suspend fun getNoticeUnreadCnt() : AppResult<Int?>

    suspend fun fetchNoticeSetting(): AppResult<NoticeSettingInfo>
    suspend fun updateNoticeSetting(body: UpdateNoticeSettingRequest): AppResult<NoticeSettingInfo>
}