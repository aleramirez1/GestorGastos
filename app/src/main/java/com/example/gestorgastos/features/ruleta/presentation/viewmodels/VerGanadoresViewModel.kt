package com.example.gestorgastos.features.ruleta.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import com.example.gestorgastos.features.ruleta.domain.entities.RuletaResult
import com.example.gestorgastos.features.ruleta.domain.usecases.ObtenerResultadosRuletaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VerGanadoresUiState(
    val resultados: List<RuletaResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class VerGanadoresViewModel @Inject constructor(
    private val obtenerResultadosRuletaUseCase: ObtenerResultadosRuletaUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerGanadoresUiState())
    val uiState = _uiState.asStateFlow()

    init {
        cargarResultados()
    }

    fun cargarResultados() {
        val usuarioId = tokenManager.getUserId()
        if (usuarioId == 0) {
            _uiState.update { it.copy(isLoading = false, resultados = emptyList()) }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                val resultados = obtenerResultadosRuletaUseCase(usuarioId)
                _uiState.update { it.copy(isLoading = false, resultados = resultados) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Error al cargar resultados: ${e.message}"
                    ) 
                }
            }
        }
    }
}
