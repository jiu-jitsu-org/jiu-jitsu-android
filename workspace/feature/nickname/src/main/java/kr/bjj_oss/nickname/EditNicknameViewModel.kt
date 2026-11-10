package kr.bjj_oss.nickname

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.bjj_oss.data.repository.*
import kr.bjj_oss.domain.isValidUserNickName
import kr.bjj_oss.model.AppResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
data class EditNicknameDestination(val nickname: String)

data class EditNicknameState(
    val input: String = "", val checked: String? = null, val busy: Boolean = false, val error: Int? = null,
)

@HiltViewModel
class EditNicknameViewModel @Inject constructor(
    private val users: UserRepository, private val community: CommunityRepository,
    private val session: SessionRepository, private val saved: SavedStateHandle,
) : ViewModel() {
    private val mutableState = MutableStateFlow(EditNicknameState())
    val state = mutableState.asStateFlow()
    private var original: String? = null
    private var request: Job? = null
    private var generation = 0
    private val completed = Channel<Boolean>(Channel.BUFFERED)
    val events = completed.receiveAsFlow()
    fun initialize(nickname: String) {
        if (original != null) return
        original = nickname
        mutableState.value = EditNicknameState(input = saved.get<String>("nicknameInput") ?: nickname)
    }
    fun input(value: String) {
        if (state.value.busy && state.value.checked != null) return
        generation++; request?.cancel()
        saved["nicknameInput"] = value
        mutableState.value = EditNicknameState(input = value)
    }
    fun submit() {
        val current = state.value
        if (current.busy) return
        if (current.input == original) { viewModelScope.launch { completed.send(false) }; return }
        if (!Regex("[가-힣a-zA-Z0-9]{2,12}").matches(current.input)) { mutableState.update { it.copy(error = R.string.nickname_title_validate_error) }; return }
        val revision = ++generation
        request = viewModelScope.launch {
            mutableState.update { it.copy(busy = true, error = null) }
            if (current.checked != current.input) {
                val result = users.checkNickname(current.input)
                if (revision != generation) return@launch
                mutableState.update { it.copy(busy = false, checked = current.input.takeIf { result is AppResult.Success && result.data },
                    error = when (result) { is AppResult.Success -> if (result.data) null else R.string.nickname_title_duplicate_error; else -> R.string.nickname_network_error }) }
            } else {
                when (users.updateNickname(current.input)) {
                    is AppResult.Success -> {
                        community.updateCachedNickname(current.input)
                        session.updateCachedProfile(current.input, session.profileImageUrl.first())
                        completed.send(true)
                    }
                    is AppResult.Failure -> mutableState.update { it.copy(error = R.string.nickname_network_error) }
                }
                mutableState.update { it.copy(busy = false) }
            }
        }
    }
}
