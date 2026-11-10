package kr.bjj_oss.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.bjj_oss.data.repository.*
import kr.bjj_oss.model.*
import kr.bjj_oss.profile.BuildConfig
import kr.bjj_oss.profile.R
import kr.bjj_oss.profile.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {
    private val mutableState = MutableStateFlow(ProfileUiState())
    val state = mutableState.asStateFlow()
    private val messages = Channel<Int>(Channel.BUFFERED)
    val events = messages.receiveAsFlow()
    private var refresh: Job? = null
    private var mutation: Job? = null
    private var sessionGeneration = 0L

    init {
        viewModelScope.launch {
            communityRepository.communityProfile.collect { profile ->
                mutableState.update { old ->
                    val groups = profile?.competitions.orEmpty().competitionGroups()
                    val keys = groups.map { it.key }
                    val draft = old.orderDraft?.let { draft ->
                        // Reconcile latest rows while preserving draft order within each pin boundary.
                        groups.groupBy { it.pinned }.entries.sortedByDescending { it.key }.flatMap { (_, rows) ->
                            val available = rows.map { it.key }
                            draft.filter { it in available } + available.filterNot { it in draft }
                        }
                    }?.takeIf { keys.isNotEmpty() }
                    old.copy(community = profile, orderDraft = draft)
                }
            }
        }
        viewModelScope.launch { sessionRepository.nickname.collect { name -> mutableState.update { it.copy(sessionNickname = name.orEmpty()) } } }
        viewModelScope.launch {
            sessionRepository.revision.collect {
                sessionGeneration++
                refresh?.cancel(); mutation?.cancel()
                val loggedIn = sessionRepository.isLoggedIn()
                mutableState.value = ProfileUiState(isGuest = !loggedIn, sessionReady = true,
                    sessionNickname = sessionRepository.nickname.first().orEmpty(),
                    community = if (loggedIn) communityRepository.communityProfile.value else null)
                if (loggedIn) refresh(initial = true)
            }
        }
    }

    fun refresh(initial: Boolean = false) {
        val old = state.value
        if (!old.sessionReady || old.isGuest || old.busy || refresh?.isActive == true) return
        val generation = sessionGeneration
        refresh = viewModelScope.launch {
            mutableState.update { it.copy(loading = initial && it.community == null, refreshing = true, loadFailed = false) }
            // A failed source does not discard the other source's successful result.
            launch {
                val result = communityRepository.getCommunityProfile()
                if (generation == sessionGeneration && result is AppResult.Failure) {
                    mutableState.update { it.copy(loadFailed = true) }
                    if (initial) messages.send(R.string.profile_load_error)
                }
            }
            launch {
                val result = userRepository.getUserProfileInfo()
                if (generation == sessionGeneration && result is AppResult.Success) mutableState.update { it.copy(user = result.data) }
            }
        }.also { job -> job.invokeOnCompletion {
            if (generation == sessionGeneration) mutableState.update { it.copy(loading = false, refreshing = false) }
        } }
    }

    private fun save(update: CommunityProfileUpdate, message: Int?) {
        if (state.value.busy || state.value.isGuest || state.value.community == null) return
        refresh?.cancel()
        mutation = viewModelScope.launch {
            mutableState.update { it.copy(saving = true) }
            try {
                when (communityRepository.modifyCommunityProfile(update)) {
                    is AppResult.Success -> if (message != null) messages.send(message)
                    is AppResult.Failure -> messages.send(R.string.profile_save_error)
                }
            } finally { mutableState.update { it.copy(saving = false, orderDraft = null) } }
        }
    }

    fun changeBelt(rank: BELT_RANK, stripe: BELT_STRIPE) = save(
        CommunityProfileUpdate(CommunityProfileField.BELT, beltRank = rank, beltStripe = stripe),
        if (state.value.community?.beltRank == null) R.string.profile_belt_created else R.string.profile_belt_updated,
    )

    fun deleteGroup(key: String) {
        if (state.value.orderDraft != null) return
        val entries = state.value.community?.competitions.orEmpty()
        val remaining = entries.filterNot { it.groupKey() == key }
        if (remaining.size == entries.size) return
        save(CommunityProfileUpdate(CommunityProfileField.COMPETITION, competitions = remaining), R.string.profile_competition_deleted)
    }

    fun togglePin(key: String) {
        if (state.value.busy || state.value.orderDraft != null) return
        val entries = state.value.community?.competitions.orEmpty()
        val groups = entries.competitionGroups()
        val target = groups.firstOrNull { it.key == key } ?: return
        if (!target.pinned && groups.count { it.pinned } >= 3) {
            viewModelScope.launch { messages.send(R.string.profile_pin_limit) }; return
        }
        val pinned = listOf(key) + groups.filter { it.pinned && it.key != key }.map { it.key }
        val updated = entries.map { entry ->
            val entryKey = entry.groupKey()
            when {
                entryKey == key -> entry.copy(isPinned = !target.pinned, displayOrder = if (target.pinned) null else 0)
                entryKey in pinned -> entry.copy(isPinned = true, displayOrder = pinned.indexOf(entryKey) - if (target.pinned) 1 else 0)
                else -> entry
            }
        }
        save(CommunityProfileUpdate(CommunityProfileField.COMPETITION, competitions = updated),
            if (target.pinned) R.string.profile_unpinned else R.string.profile_pinned)
    }

    fun moveGroup(key: String, direction: Int) {
        if (state.value.busy) return
        val groups = state.value.community?.competitions.orEmpty().competitionGroups()
        val keys = (state.value.orderDraft ?: groups.map { it.key }).toMutableList()
        val from = keys.indexOf(key); val to = from + direction
        if (from < 0 || to !in keys.indices) return
        val byKey = groups.associateBy { it.key }
        if (byKey[key]?.pinned != byKey[keys[to]]?.pinned) return
        keys.add(to, keys.removeAt(from))
        mutableState.update { it.copy(orderDraft = keys) }
    }
    fun beginOrder() {
        if (!state.value.busy) mutableState.update { it.copy(orderDraft = it.community?.competitions.orEmpty().competitionGroups().map { group -> group.key }) }
    }
    fun cancelOrder() { mutableState.update { it.copy(orderDraft = null) } }
    fun saveOrder() {
        val entries = state.value.community?.competitions.orEmpty()
        val draft = state.value.orderDraft ?: return
        val groups = entries.competitionGroups()
        val changed = setOf(true, false).filter { pin ->
            draft.filter { key -> groups.any { it.key == key && it.pinned == pin } } != groups.filter { it.pinned == pin }.map { it.key }
        }.toSet()
        if (changed.isEmpty()) { cancelOrder(); return }
        save(CommunityProfileUpdate(CommunityProfileField.COMPETITION, competitions = entries.inGroupOrder(draft, changed)), null)
    }

    fun uploadImage(uri: String, verification: Boolean) {
        if (state.value.busy || state.value.isGuest || state.value.user == null) return
        refresh?.cancel()
        mutation = viewModelScope.launch {
            mutableState.update { it.copy(imageOperation = if (verification) ImageOperation.VERIFICATION else ImageOperation.UPLOAD) }
            try {
                val result = if (BuildConfig.IMAGE_PUBLIC_KEY.isBlank()) null else if (verification)
                    imageRepository.uploadVerificationImage(uri, BuildConfig.IMAGE_PUBLIC_KEY)
                else imageRepository.uploadCommunityImage(uri, BuildConfig.IMAGE_PUBLIC_KEY)
                when (result) {
                    is AppResult.Success -> {
                        val image = result.data
                        mutableState.update { old -> old.copy(user = old.user?.let { user ->
                            if (verification) user.copy(ownerVerification = OwnerVerification(VerificationStatus.PENDING, image.imageUrl))
                            else user.copy(profileImageUrl = image.imageUrl, profileImageFileId = image.id)
                        }) }
                        if (!verification) sessionRepository.updateCachedProfile(state.value.nickname, image.imageUrl)
                        messages.send(if (verification) R.string.profile_verification_sent else R.string.profile_image_updated)
                    }
                    else -> messages.send(if (result is AppResult.Failure && result.error.serverCode == "IMAGE_LINK") {
                        if (verification) R.string.profile_verification_error else R.string.profile_image_save_error
                    } else R.string.profile_image_upload_error)
                }
            } finally { mutableState.update { it.copy(imageOperation = null) } }
        }
    }

    fun deleteImage() {
        val user = state.value.user ?: return
        val id = user.profileImageFileId ?: run { viewModelScope.launch { messages.send(R.string.profile_image_unavailable) }; return }
        if (state.value.busy || state.value.isGuest) return
        refresh?.cancel()
        mutation = viewModelScope.launch {
            mutableState.update { it.copy(imageOperation = ImageOperation.DELETE) }
            try {
                when (imageRepository.deleteProfileImage(id)) {
                    is AppResult.Success -> {
                        mutableState.update { it.copy(user = user.copy(profileImageUrl = null, profileImageFileId = null)) }
                        sessionRepository.updateCachedProfile(state.value.nickname, null)
                        messages.send(R.string.profile_image_deleted)
                    }
                    is AppResult.Failure -> messages.send(R.string.profile_image_save_error)
                }
            } finally { mutableState.update { it.copy(imageOperation = null) } }
        }
    }
}
