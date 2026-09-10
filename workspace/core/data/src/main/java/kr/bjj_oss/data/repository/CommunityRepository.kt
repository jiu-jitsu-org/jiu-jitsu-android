package kr.bjj_oss.data.repository

import kr.bjj_oss.model.AppResult
import kr.bjj_oss.model.CommunityProfileInfo
import kr.bjj_oss.model.CommunityProfileUpdate
import kotlinx.coroutines.flow.StateFlow

/**
 * Single source of truth for the community profile.
 *
 * The observable cache belongs to the repository so every profile screen receives the same
 * immutable snapshot without relying on a process-global mutable singleton.
 */
interface CommunityRepository {
    val communityProfile: StateFlow<CommunityProfileInfo?>

    suspend fun getCommunityProfile(): AppResult<CommunityProfileInfo>

    suspend fun modifyCommunityProfile(
        update: CommunityProfileUpdate,
    ): AppResult<CommunityProfileInfo>

    /** Clears user-scoped memory when the authenticated session changes or ends. */
    fun clearCachedProfile()
}
