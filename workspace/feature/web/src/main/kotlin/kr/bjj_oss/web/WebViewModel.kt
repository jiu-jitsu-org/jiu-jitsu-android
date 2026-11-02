package kr.bjj_oss.web

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.bjj_oss.data.repository.SessionRepository
import kr.bjj_oss.data.repository.WebSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kr.bjj_oss.data.repository.NoticeRepository
import javax.inject.Inject

@HiltViewModel
class WebViewModel @Inject constructor(
    private val sessions: SessionRepository,
    private val webSessions: WebSessionRepository,
    private val  noticeRepository: NoticeRepository,
) : ViewModel() {
    val origin get() = webSessions.origin
    val debuggingEnabled get() = webSessions.debuggingEnabled
    val revisions get() = sessions.revision

    suspend fun revision() = sessions.revision.first()
    suspend fun token() = sessions.accessToken.first()
    suspend fun prepare() = webSessions.prepare()
    suspend fun refresh(token: String?) = sessions.refreshAccessToken(token)
    suspend fun logout() = sessions.clearSession()

    fun unreadNoticeCnt() {
        viewModelScope.launch {
            noticeRepository.getNoticeUnreadCnt()
        }
    }
}
