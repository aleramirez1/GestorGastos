package com.example.gestorgastos.features.personas.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import com.example.gestorgastos.features.personas.domain.entities.PersonaGanadora
import com.example.gestorgastos.features.personas.domain.usecases.ObtenerPersonasGanadorasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PersonasUiState(
    val personas: List<PersonaGanadora> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PersonasViewModel @Inject constructor(
    private val obtenerPersonasGanadorasUseCase: ObtenerPersonasGanadorasUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonasUiState())
    val uiState = _uiState.asStateFlow()

    init {
        cargarPersonas()
    }

    fun cargarPersonas() {
        val usuarioId = tokenManager.getUserId()
        if (usuarioId == 0) {
            _uiState.update { it.copy(isLoading = false, personas = emptyList()) }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                val personas = obtenerPersonasGanadorasUseCase(usuarioId)
                _uiState.update { it.copy(isLoading = false, personas = personas) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar personas: ${e.message}"
                    )
                }
            }
        }
    }
}
