package com.example.branchtake_home.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Data.BranchRepository
import com.example.Data.Remote.MessageDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ThreadItem(
    val threadId: Long,
    val userId: Long?,
    val last: MessageDto
)

// One-shot UI events (snackbar, etc.)
sealed class ThreadsEvent {
    data class Snackbar(val message: String) : ThreadsEvent()
}

@HiltViewModel
class ThreadsViewModel @Inject constructor(
    private val repo: BranchRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<ThreadItem>>>(UiState.Loading)
    val state: StateFlow<UiState<List<ThreadItem>>> = _state

    // Event stream for snackbars
    private val _events = MutableSharedFlow<ThreadsEvent>()
    val events = _events.asSharedFlow()

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _state.value = UiState.Loading
        runCatching {
            val all = repo.getMessages()
            // Group by thread and take the newest message as "last"
            all.groupBy { it.threadId }.values.map { list ->
                val last = requireNotNull(list.maxByOrNull { it.timestamp })
                ThreadItem(
                    threadId = last.threadId,
                    userId = last.userId,
                    last = last
                )
            }.sortedByDescending { it.last.timestamp }
        }.onSuccess { _state.value = UiState.Success(it) }
            .onFailure {
                _state.value = UiState.Error(it.message ?: "Failed to load")
                // Optional: also surface a snackbar on load failure
                _events.emit(ThreadsEvent.Snackbar("Failed to load conversations"))
            }
    }

    // Non-suspending wrapper for UI
    fun logout() = viewModelScope.launch {
        runCatching { repo.logout() }
    }

    fun resetAgentMessages() = viewModelScope.launch {
        runCatching { repo.reset() }
            .onSuccess {
                _events.emit(ThreadsEvent.Snackbar("Your sent messages were reset."))
                refresh()
            }
            .onFailure {
                _state.value = UiState.Error(it.message ?: "Reset failed")
                _events.emit(ThreadsEvent.Snackbar("Reset failed, please try again."))
            }
    }
}
