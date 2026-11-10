package kr.bjj_oss.profile.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.bjj_oss.data.repository.CommunityRepository
import kr.bjj_oss.model.*
import kr.bjj_oss.profile.R
import kr.bjj_oss.profile.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Year
import javax.inject.Inject

enum class CompetitionStep { RESULT, NAME, YEAR, DIVISION, COMPLETE }
data class CompetitionEditorState(
    val step: CompetitionStep = CompetitionStep.RESULT,
    val name: String = "", val year: Int = Year.now().value, val rank: String = "GOLD",
    val uniform: String? = "GI", val belt: String? = null,
    val weight: String = "", val unlimited: Boolean = false, val absolute: Boolean = false,
    val competitionId: Int? = null, val fixedGroup: Boolean = false, val editing: Boolean = false,
    val saving: Boolean = false, val error: Int? = null,
    val suggestions: List<CompetitionSuggestion> = emptyList(), val searchFailed: Boolean = false,
    val completedEntries: List<CompetitionInfo> = emptyList(),
)

@HiltViewModel
class ModifyCompetitionViewModel @Inject constructor(
    private val repository: CommunityRepository,
    private val saved: SavedStateHandle
) : ViewModel() {
    private val mutableState = MutableStateFlow(CompetitionEditorState())
    val state = mutableState.asStateFlow()
    private val messages = Channel<EditorResult>(Channel.BUFFERED)
    val events = messages.receiveAsFlow()
    private var initialized = false
    private var original: CompetitionInfo? = null
    private var originalIndex = -1
    private var touched = mutableSetOf<String>()
    private var search: Job? = null
    suspend fun initialize(index: Int, key: String?) {
        if (initialized) return
        initialized = true; originalIndex = index
        if (repository.communityProfile.value == null) {
            mutableState.update { it.copy(saving = true) }
            repository.getCommunityProfile()
        }
        val profile = repository.communityProfile.value
        val entries = profile?.competitions.orEmpty()
        original = entries.getOrNull(index)
        val group = entries.competitionGroups().find { it.key == key }
        val first = original ?: group?.first
        val latest = entries.withIndex().filter { it.value.weightBound != null }
            .maxWithOrNull(compareBy<IndexedValue<CompetitionInfo>> {
                it.value.competitionYear ?: 0
            }.thenBy { it.index })?.value
        val lastDivision = group?.entries?.lastOrNull()?.value
        val defaults = CompetitionEditorState(
            name = first?.competitionName.orEmpty(),
            year = first?.competitionYear ?: Year.now().value,
            rank = original?.competitionRank?.name ?: "GOLD",
            uniform = if (original != null) original?.uniform?.name else (lastDivision?.uniform
                ?: CompetitionUniform.GI).name,
            belt = if (original != null) original?.belt?.name else (lastDivision?.belt
                ?: profile?.beltRank)?.name,
            weight = (if (original != null) original?.weightLimit else latest?.weightLimit)?.toString()
                .orEmpty(),
            unlimited = (if (original != null) original?.weightBound else latest?.weightBound) == CompetitionWeightBound.OVER,
            absolute = original?.absolute ?: false,
            competitionId = first?.competitionId,
            fixedGroup = group != null,
            editing = index >= 0,
        )
        touched =
            saved.get<ArrayList<String>>("competitionTouched")?.toMutableSet() ?: mutableSetOf()
        mutableState.value =
            if (saved.get<Boolean>("competitionInitialized") == true) defaults.copy(
                step = CompetitionStep.entries.getOrElse(
                    saved.get<Int>("competitionStep") ?: 0
                ) { CompetitionStep.RESULT },
                name = saved.get<String>("competitionName").orEmpty(),
                year = saved["competitionYear"] ?: defaults.year,
                rank = saved["competitionRank"] ?: defaults.rank,
                uniform = saved["competitionUniform"],
                belt = saved["competitionBelt"],
                weight = saved.get<String>("competitionWeight").orEmpty(),
                unlimited = saved["competitionUnlimited"] ?: false,
                absolute = saved["competitionAbsolute"] ?: false,
                competitionId = saved["competitionId"],
                completedEntries = entries.filter {
                    it.competitionYear == saved.get<Int>("competitionYear") && it.competitionName == saved.get<String>(
                        "competitionName"
                    )
                },
            ) else defaults
        if (profile == null || index >= 0 && original == null || key != null && group == null)
            mutableState.update { it.copy(error = R.string.profile_division_removed) }
    }

    private fun update(block: (CompetitionEditorState) -> CompetitionEditorState) {
        mutableState.update(block)
        val current = state.value
        saved["competitionInitialized"] = true; saved["competitionStep"] = current.step.ordinal
        saved["competitionName"] = current.name; saved["competitionYear"] =
            current.year; saved["competitionRank"] = current.rank
        saved["competitionUniform"] = current.uniform; saved["competitionBelt"] = current.belt
        saved["competitionWeight"] = current.weight; saved["competitionUnlimited"] =
            current.unlimited
        saved["competitionAbsolute"] = current.absolute; saved["competitionId"] =
            current.competitionId
        saved["competitionTouched"] = ArrayList(touched)
    }

    fun name(value: String) {
        if (state.value.fixedGroup || state.value.saving) return
        update {
            it.copy(
                name = value,
                competitionId = null,
                error = null,
                suggestions = emptyList(),
                searchFailed = false
            )
        }
        search?.cancel()
        if (value.isBlank()) return
        search = viewModelScope.launch {
            delay(300)
            val result = repository.searchCompetitions(value.trim())
            if (state.value.name != value) return@launch
            update {
                it.copy(suggestions = if (result is AppResult.Success) result.data.sortedByDescending { candidate ->
                    normalize(
                        candidate.name
                    ).startsWith(normalize(value))
                } else emptyList(), searchFailed = result is AppResult.Failure)
            }
        }
    }

    private fun normalize(value: String) = value.filterNot(Char::isWhitespace).lowercase()
    fun suggestion(value: CompetitionSuggestion) {
        search?.cancel()
        update {
            it.copy(
                name = value.name,
                year = value.year ?: it.year,
                competitionId = value.id,
                suggestions = emptyList(),
                searchFailed = false
            )
        }
    }

    fun year(value: Int) {
        if (!state.value.fixedGroup) update {
            it.copy(
                year = value,
                competitionId = if (value == it.year) it.competitionId else null
            )
        }
    }

    fun rank(value: String) {
        update { it.copy(rank = value) }
    }

    fun uniform(value: String?) {
        touched += "uniform"; update { it.copy(uniform = value) }
    }

    fun belt(value: String?) {
        touched += "belt"; update { it.copy(belt = value) }
    }

    fun weight(value: String) {
        val normalized = value.replace(',', '.')
        if (!Regex("[0-9]{0,3}(\\.[0-9]?)?").matches(normalized)) return
        touched += "weight"; update {
            it.copy(
                weight = if (normalized.startsWith('.')) "0$normalized" else normalized,
                error = null
            )
        }
    }

    fun unlimited(value: Boolean) {
        touched += "weight"; update {
            it.copy(
                unlimited = value,
                absolute = if (value) false else it.absolute
            )
        }
    }

    fun absolute(value: Boolean) {
        touched += "absolute"; touched += "weight"; update {
            it.copy(
                absolute = value,
                unlimited = if (value) false else it.unlimited
            )
        }
    }

    fun back(onClose: () -> Unit) {
        if (state.value.saving) return
        when (state.value.step) {
            CompetitionStep.RESULT, CompetitionStep.COMPLETE -> onClose()
            CompetitionStep.NAME -> update { it.copy(step = CompetitionStep.RESULT) }
            CompetitionStep.YEAR -> update { it.copy(step = CompetitionStep.NAME) }
            CompetitionStep.DIVISION -> update { it.copy(step = if (it.fixedGroup) CompetitionStep.RESULT else CompetitionStep.YEAR) }
        }
    }

    fun next() {
        if (state.value.saving) return
        when (state.value.step) {
            CompetitionStep.RESULT -> update { it.copy(step = if (it.fixedGroup) CompetitionStep.DIVISION else CompetitionStep.NAME) }
            CompetitionStep.NAME -> if (state.value.name.trim()
                    .isNotEmpty()
            ) update { it.copy(step = CompetitionStep.YEAR, error = null) }

            CompetitionStep.YEAR -> update { it.copy(step = CompetitionStep.DIVISION) }
            CompetitionStep.DIVISION -> save(false)
            CompetitionStep.COMPLETE -> Unit
        }
    }

    fun save(skip: Boolean) {
        val form = state.value
        if (form.saving || form.name.isBlank() || form.error == R.string.profile_division_removed) return
        val current = repository.communityProfile.value?.competitions.orEmpty()
        val under = form.weight.toDoubleOrNull()?.takeIf { it > 0 }
        val bound =
            if (form.absolute) null else if (form.unlimited) CompetitionWeightBound.OVER else if (under != null) CompetitionWeightBound.UNDER else null
        val group =
            current.firstOrNull { it.competitionYear == form.year && it.competitionName == form.name }
        val entry = CompetitionInfo(
            competitionYear = form.year,
            competitionName = form.name.trim(),
            competitionRank = form.rank.toCompetitionRank(),
            uniform = if (skip && "uniform" !in touched) null else CompetitionUniform.entries.firstOrNull { it.name == form.uniform },
            belt = if (skip && "belt" !in touched) null else form.belt.toBeltRank(),
            weightBound = if (skip && "weight" !in touched) null else bound,
            weightLimit = if (skip && "weight" !in touched || form.absolute) null else if (bound == CompetitionWeightBound.OVER) original?.weightLimit?.takeIf { original?.weightBound == CompetitionWeightBound.OVER } else under,
            absolute = form.absolute && (!skip || "absolute" in touched),
            competitionId = form.competitionId,
            isPinned = original?.isPinned ?: group?.isPinned ?: false,
            displayOrder = original?.displayOrder ?: group?.displayOrder,
        )
        val updated = current.toMutableList()
        if (form.editing) {
            val index =
                if (updated.getOrNull(originalIndex) == original) originalIndex else updated.indexOf(
                    original
                )
            if (index < 0) {
                update { it.copy(error = R.string.profile_division_removed) }; return
            }
            updated[index] = entry
        } else updated += entry
        viewModelScope.launch {
            update { it.copy(saving = true, error = null) }
            when (val result = repository.modifyCommunityProfile(
                CommunityProfileUpdate(
                    CommunityProfileField.COMPETITION,
                    competitions = updated
                )
            )) {
                is AppResult.Failure -> update {
                    it.copy(
                        saving = false,
                        error = R.string.profile_save_error
                    )
                }

                is AppResult.Success -> {
                    messages.send(
                        EditorResult(
                            if (form.editing) R.string.profile_competition_updated else R.string.profile_competition_added,
                            close = form.editing
                        )
                    )
                    update {
                        it.copy(
                            saving = false, step = CompetitionStep.COMPLETE,
                            completedEntries = result.data.competitions.orEmpty()
                                .filter { item -> item.groupKey() == entry.groupKey() })
                    }
                }
            }
        }
    }

    fun delete() {
        if (!state.value.editing || state.value.saving) return
        val current = repository.communityProfile.value?.competitions.orEmpty().toMutableList()
        val index =
            if (current.getOrNull(originalIndex) == original) originalIndex else current.indexOf(
                original
            )
        if (index < 0) {
            update { it.copy(error = R.string.profile_division_removed) }; return
        }
        current.removeAt(index)
        viewModelScope.launch {
            update { it.copy(saving = true, error = null) }
            when (repository.modifyCommunityProfile(
                CommunityProfileUpdate(
                    CommunityProfileField.COMPETITION,
                    competitions = current
                )
            )) {
                is AppResult.Success -> messages.send(EditorResult(R.string.profile_competition_deleted))
                is AppResult.Failure -> update { it.copy(error = R.string.profile_save_error) }
            }
            update { it.copy(saving = false) }
        }
    }

    fun anotherDivision() {
        touched.clear()
        update {
            it.copy(
                step = CompetitionStep.RESULT,
                fixedGroup = true,
                editing = false,
                rank = "GOLD",
                error = null
            )
        }
    }
}
