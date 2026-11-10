package kr.bjj_oss.profile.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.bjj_oss.data.repository.CommunityRepository
import kr.bjj_oss.model.CommunityProfileField
import kr.bjj_oss.model.CommunityProfileUpdate
import kr.bjj_oss.model.AppResult
import kr.bjj_oss.model.toPosition
import kr.bjj_oss.model.toTechnique
import kr.bjj_oss.model.toSubmission
import kr.bjj_oss.profile.R
import kr.bjj_oss.profile.model.EditorResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StyleEditorState(
    val page: Int = 0,
    val selected: String? = null,
    val bestSelected: String? = null,
    val bestEdited: Boolean = false,
    val selectedTab: Int = 0,
    val saving: Boolean = false,
    val error: Boolean = false,
    val ready: Boolean = false,
) {
    val visibleSelection: String? get() = if (selectedTab == 0) bestSelected else selected
}

@HiltViewModel
class ModifyMyStyleViewModel @Inject constructor(
    private val repository: CommunityRepository,
    private val saved: SavedStateHandle,
) : ViewModel() {
    private val mutableState = MutableStateFlow(StyleEditorState())
    val state = mutableState.asStateFlow()
    private val results = Channel<EditorResult>(Channel.BUFFERED)
    val events = results.receiveAsFlow()
    private var all = false
    private var initialized = false

    suspend fun initialize(type: String) {
        if (initialized) return
        initialized = true
        if (repository.communityProfile.value == null) {
            mutableState.update { it.copy(saving = true) }
            if (repository.getCommunityProfile() is AppResult.Failure) {
                mutableState.update { it.copy(saving = false, error = true) }
                initialized = false
                return
            }
        }
        all = type == "all"
        val page = saved.get<Int>("stylePage") ?: when (type) {
            "technique" -> 1
            "submission" -> 2
            else -> 0
        }
        mutableState.value = currentPage(page).copy(
            selected = if (saved.get<Boolean>("selectionEdited") == true) saved["styleSelection"] else currentSelection(page),
            bestSelected = if (saved.get<Boolean>("bestSelectionEdited") == true) saved["bestStyleSelection"] else currentSelection(page, best = true),
            bestEdited = saved.get<Boolean>("bestSelectionEdited") == true,
            selectedTab = (saved.get<Int>("styleTab") ?: 0).coerceIn(0, 1),
        )
    }

    fun retry(type: String) {
        viewModelScope.launch { initialize(type) }
    }

    private fun currentSelection(page: Int, best: Boolean = false): String? = repository.communityProfile.value?.let {
        when (page) {
            0 -> if (best) it.bestPosition?.name else it.favoritePosition?.name
            1 -> if (best) it.bestTechnique?.name else it.favoriteTechnique?.name
            else -> if (best) it.bestSubmission?.name else it.favoriteSubmission?.name
        }
    }

    private fun currentPage(page: Int) = StyleEditorState(
        page = page,
        selected = currentSelection(page),
        bestSelected = currentSelection(page, best = true),
        ready = true,
    )

    fun selectTab(index: Int) {
        if (state.value.saving || !state.value.ready || index !in 0..1) return
        saved["styleTab"] = index
        mutableState.update { it.copy(selectedTab = index) }
    }

    fun select(wire: String?) {
        val current = state.value
        if (current.saving || !current.ready) return
        if (current.selectedTab == 0) {
            // Best has no clear operation in the existing repository contract. Never turn a
            // preview's fallback artwork into a write until a real choice has been made.
            if (wire == null) return
            saved["bestSelectionEdited"] = true
            saved["bestStyleSelection"] = wire
            mutableState.update { it.copy(bestSelected = wire, bestEdited = true, error = false) }
        } else {
            // Tapping the selected favorite again preserves the current clear-selection behavior.
            val value = wire.takeUnless { it == current.selected }
            saved["selectionEdited"] = true
            saved["styleSelection"] = value
            mutableState.update { it.copy(selected = value, error = false) }
        }
    }

    fun completeSelection() {
        if (state.value.saving || !state.value.ready) return
        if (state.value.selectedTab == 0) selectTab(1) else save()
    }

    fun save() {
        val before = state.value
        if (before.saving || !before.ready) return
        viewModelScope.launch {
            mutableState.update { it.copy(saving = true, error = false) }
            val best = before.bestSelected.takeIf { before.bestEdited }
            val update = when (before.page) {
                0 -> CommunityProfileUpdate(
                    CommunityProfileField.POSITION,
                    bestPosition = best.toPosition(),
                    favoritePosition = before.selected.toPosition(),
                )
                1 -> CommunityProfileUpdate(
                    CommunityProfileField.TECHNIQUE,
                    bestTechnique = best.toTechnique(),
                    favoriteTechnique = before.selected.toTechnique(),
                )
                else -> CommunityProfileUpdate(
                    CommunityProfileField.SUBMISSION,
                    bestSubmission = best.toSubmission(),
                    favoriteSubmission = before.selected.toSubmission(),
                )
            }
            when (repository.modifyCommunityProfile(update)) {
                is AppResult.Failure -> mutableState.update { it.copy(saving = false, error = true) }
                is AppResult.Success -> {
                    val next = all && before.page < 2
                    results.send(EditorResult(
                        if (all && !next) R.string.profile_all_styles_saved else when (before.page) {
                            0 -> R.string.profile_position_saved
                            1 -> R.string.profile_technique_saved
                            else -> R.string.profile_submission_saved
                        },
                        close = !next,
                    ))
                    val page = if (next) before.page + 1 else before.page
                    saved["stylePage"] = page
                    saved["selectionEdited"] = false
                    saved["bestSelectionEdited"] = false
                    saved["styleTab"] = 0
                    mutableState.value = currentPage(page)
                }
            }
        }
    }
}
