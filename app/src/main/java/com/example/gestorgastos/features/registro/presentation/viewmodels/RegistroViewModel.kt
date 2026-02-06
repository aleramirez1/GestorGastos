package com.example.gestorgastos.features.registro.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorgastos.features.registro.domain.usecases.RegistroUseCase
import com.example.gestorgastos.features.registro.presentation.screens.RegistroUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroViewModel(private val registroUseCase: RegistroUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState = _uiState.asStateFlow()

    fun registro(nombre: String, email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = registroUseCase(nombre, email, password)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { currentState.copy(isLoading = false, isSuccess = true) },
                    onFailure = { currentState.copy(isLoading = false, error = it.message) }
                )
            }
        }
    }

    fun resetState() {
        _uiState.update { RegistroUiState() }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
