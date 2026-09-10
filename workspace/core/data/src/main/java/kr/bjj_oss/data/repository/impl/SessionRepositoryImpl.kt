package kr.bjj_oss.data.repository.impl

import kr.bjj_oss.data.repository.CommunityRepository
import kr.bjj_oss.data.repository.SessionRepository
import kr.bjj_oss.data.session.SessionLocalDataSource
import kr.bjj_oss.data.session.TokenRefreshCoordinator
import kr.bjj_oss.model.SessionUpdate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class SessionRepositoryImpl @Inject constructor(
    private val localDataSource: SessionLocalDataSource,
    private val refreshCoordinator: TokenRefreshCoordinator,
    private val communityRepository: CommunityRepository,
) : SessionRepository {
    override val revision: Flow<Long> = localDataSource.revision
    override suspend fun refreshAccessToken(expiredAccessToken: String?) = refreshCoordinator.refresh(expiredAccessToken)

    override val accessToken: Flow<String?> = localDataSource.accessToken
    override val nickname: Flow<String?> = localDataSource.nickname
    override val profileImageUrl: Flow<String?> = localDataSource.profileImageUrl

    override suspend fun isLoggedIn(): Boolean = localDataSource.isLoggedIn()

    override suspend fun updateSession(update: SessionUpdate) {
        // A new token may belong to a different account. Clear user-scoped memory before the new
        // session becomes visible so the next screen never renders the previous user's profile.
        communityRepository.clearCachedProfile()
        localDataSource.updateSession(update)
    }

    override suspend fun updateCachedProfile(nickname: String?, profileImageUrl: String?) {
        localDataSource.updateCachedProfile(nickname, profileImageUrl)
    }

    override fun setTransientAccessToken(accessToken: String?) {
        localDataSource.setTransientAccessToken(accessToken)
    }

    override suspend fun clearSession() {
        localDataSource.clear()
        communityRepository.clearCachedProfile()
    }
}
