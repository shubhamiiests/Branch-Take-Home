package com.example.branchtake_home.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Data.BranchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: BranchRepository
) : ViewModel() {

    val token = repo.authToken.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    private val _loginState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val loginState: StateFlow<UiState<Unit>> = _loginState

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginState.value = UiState.Loading
        runCatching { repo.login(email, password) }
            .onSuccess { _loginState.value = UiState.Success(Unit) }
            .onFailure { _loginState.value = UiState.Error(it.message ?: "Login failed") }
    }

    fun logout() = viewModelScope.launch { repo.logout() }
}