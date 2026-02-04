package com.example.gestorgastos.features.gastos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorgastos.features.gastos.domain.usecases.LoginUseCase
import com.example.gestorgastos.features.gastos.domain.usecases.RegistroUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registroUseCase: RegistroUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = loginUseCase(email, password)
            _uiState.update { state ->
                result.fold(
                    onSuccess = { state.copy(isLoading = false, isSuccess = true) },
                    onFailure = { state.copy(isLoading = false, error = it.message) }
                )
            }
        }
    }

    fun registro(nombre: String, email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = registroUseCase(nombre, email, password)
            _uiState.update { state ->
                result.fold(
                    onSuccess = { state.copy(isLoading = false, isSuccess = true) },
                    onFailure = { state.copy(isLoading = false, error = it.message) }
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
