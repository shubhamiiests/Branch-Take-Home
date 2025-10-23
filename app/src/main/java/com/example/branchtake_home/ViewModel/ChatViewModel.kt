package com.example.branchtake_home.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Data.BranchRepository
import com.example.Data.Remote.MessageDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: BranchRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<MessageDto>>>(UiState.Loading)
    val state: StateFlow<UiState<List<MessageDto>>> = _state

    fun load(threadId: Long) = viewModelScope.launch {
        _state.value = UiState.Loading
        runCatching {
            repo.getMessages().filter { it.threadId == threadId }
                .sortedBy { it.timestamp }
        }.onSuccess { _state.value = UiState.Success(it) }
            .onFailure { _state.value = UiState.Error(it.message ?: "Failed to load messages") }
    }

    fun send(threadId: Long, text: String) = viewModelScope.launch {
        runCatching { repo.sendMessage(threadId, text) }
            .onSuccess { load(threadId) }
            .onFailure { /* surface error to UI if desired */ }
    }
}
