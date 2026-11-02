package kr.bjj_oss.data.repository

import kr.bjj_oss.model.AppResult
import kr.bjj_oss.model.NoticeUnreadCountInfo

interface NoticeRepository {
    suspend fun getNoticeUnreadCnt() : AppResult<NoticeUnreadCountInfo>
}