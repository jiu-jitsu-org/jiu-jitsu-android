package com.kyu.jiu_jitsu.data.repository.impl

import com.kyu.jiu_jitsu.data.repository.CommunityRepository
import com.kyu.jiu_jitsu.data.repository.SessionRepository
import com.kyu.jiu_jitsu.data.session.SessionLocalDataSource
import com.kyu.jiu_jitsu.model.SessionUpdate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class SessionRepositoryImpl @Inject constructor(
    private val localDataSource: SessionLocalDataSource,
    private val communityRepository: CommunityRepository,
) : SessionRepository {
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
