package kr.bjj_oss.data.repository.impl

import kr.bjj_oss.data.api.CommunityService
import kr.bjj_oss.data.api.common.mapEnvelope
import kr.bjj_oss.data.api.common.safeApiCall
import kr.bjj_oss.data.model.dto.request.toRequest
import kr.bjj_oss.data.model.dto.response.toInfo
import kr.bjj_oss.data.repository.CommunityRepository
import kr.bjj_oss.model.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

internal class CommunityRepositoryImpl @Inject constructor(
    private val communityService: CommunityService,
) : CommunityRepository {
    private val mutableCommunityProfile = MutableStateFlow<CommunityProfileInfo?>(null)
    override val communityProfile = mutableCommunityProfile.asStateFlow()
    private val mutex = Mutex()
    private val cacheLock = Any()
    private var generation = 0L

    override suspend fun getCommunityProfile(): AppResult<CommunityProfileInfo> {
        val revision = synchronized(cacheLock) { generation }
        return mutex.withLock {
            if (synchronized(cacheLock) { revision != generation }) throw CancellationException("Profile session changed")
            safeApiCall { communityService.reqCommunityProfile() }.mapEnvelope { it.toInfo() }
                .also { cache(it, revision) }
        }
    }

    override suspend fun modifyCommunityProfile(update: CommunityProfileUpdate): AppResult<CommunityProfileInfo> {
        val revision = synchronized(cacheLock) { generation }
        return mutex.withLock {
            if (synchronized(cacheLock) { revision != generation }) throw CancellationException("Profile session changed")
            val current = communityProfile.value
            // Other sections, including old weight and best-style values, survive an edit.
            val command = update.copy(
                beltRank = update.beltRank ?: current?.beltRank,
                beltStripe = update.beltStripe ?: current?.beltStripe,
                gender = update.gender ?: current?.gender,
                weightKg = update.weightKg ?: current?.weightKg,
                isWeightHidden = update.isWeightHidden ?: current?.isWeightHidden,
                academyName = update.academyName ?: current?.academyName,
                bestPosition = update.bestPosition ?: current?.bestPosition,
                bestTechnique = update.bestTechnique ?: current?.bestTechnique,
                bestSubmission = update.bestSubmission ?: current?.bestSubmission,
                favoritePosition = if (update.field == CommunityProfileField.POSITION) update.favoritePosition else current?.favoritePosition,
                favoriteTechnique = if (update.field == CommunityProfileField.TECHNIQUE) update.favoriteTechnique else current?.favoriteTechnique,
                favoriteSubmission = if (update.field == CommunityProfileField.SUBMISSION) update.favoriteSubmission else current?.favoriteSubmission,
                teachingPhilosophy = current?.teachingPhilosophy,
                teachingStartDate = current?.teachingStartDate,
                teachingDetail = current?.teachingDetail,
            )
            safeApiCall { communityService.modifyCommunityProfile(command.toRequest()) }
                .mapEnvelope { it.toInfo() }.also { cache(it, revision) }
        }
    }

    override suspend fun searchCompetitions(keyword: String): AppResult<List<CompetitionSuggestion>> =
        safeApiCall { communityService.searchCompetitions(keyword) }.mapEnvelope { list ->
            list.map { CompetitionSuggestion(it.competitionId, it.name, it.year) }
        }

    override fun updateCachedNickname(nickname: String) = synchronized(cacheLock) {
        generation++
        mutableCommunityProfile.value = mutableCommunityProfile.value?.copy(nickname = nickname)
    }

    override fun clearCachedProfile() = synchronized(cacheLock) {
        generation++
        mutableCommunityProfile.value = null
    }

    private fun cache(result: AppResult<CommunityProfileInfo>, revision: Long) = synchronized(cacheLock) {
        if (revision == generation && result is AppResult.Success) mutableCommunityProfile.value = result.data
    }
}
