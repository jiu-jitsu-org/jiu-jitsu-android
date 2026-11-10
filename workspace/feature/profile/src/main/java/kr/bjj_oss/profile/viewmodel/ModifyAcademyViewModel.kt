package kr.bjj_oss.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.bjj_oss.data.repository.CommunityRepository
import kr.bjj_oss.model.*
import kr.bjj_oss.profile.R
import kr.bjj_oss.profile.model.EditorResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AcademyEditorState(val saving: Boolean = false, val error: Boolean = false)

@HiltViewModel
class ModifyAcademyViewModel @Inject constructor(private val repository: CommunityRepository) :
    ViewModel() {
    private val mutableState = MutableStateFlow(AcademyEditorState())
    val state = mutableState.asStateFlow()
    private val results = Channel<EditorResult>(Channel.BUFFERED)
    val events = results.receiveAsFlow()
    fun save(name: String, delete: Boolean = false) {
        if (state.value.saving) return
        val normalized = name.trim()
        if (!delete && !isValidAcademy(normalized)) return
        val wasEmpty = repository.communityProfile.value?.academyName.isNullOrBlank()
        viewModelScope.launch {
            mutableState.value = AcademyEditorState(saving = true)
            when (repository.modifyCommunityProfile(
                CommunityProfileUpdate(
                    CommunityProfileField.ACADEMY,
                    academyName = if (delete) "" else normalized
                )
            )) {
                is AppResult.Success -> results.send(EditorResult(if (delete) R.string.profile_academy_deleted else if (wasEmpty) R.string.profile_academy_created else R.string.profile_updated))
                is AppResult.Failure -> mutableState.value = AcademyEditorState(error = true)
            }
            mutableState.update { it.copy(saving = false) }
        }
    }
}

fun isValidAcademy(name: String) =
    name.trim().length in 1..100 && Regex("[가-힣a-zA-Z0-9\\s]+").matches(name.trim())
