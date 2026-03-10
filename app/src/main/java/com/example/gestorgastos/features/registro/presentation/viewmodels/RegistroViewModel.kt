package com.example.gestorgastos.features.registro.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorgastos.features.registro.domain.usecases.RegistroUseCase
import com.example.gestorgastos.features.registro.presentation.screens.RegistroUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val registroUseCase: RegistroUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState = _uiState.asStateFlow()


    fun registro(nombre: String, email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        if (nombre.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            return
        }

        _uiState.update { it.copy(isLoading = false, error = "Todos los campos son requeridos") }
    }

    fun resetState() {
        _uiState.update { RegistroUiState() }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}