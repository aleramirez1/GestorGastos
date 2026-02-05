package com.example.gestorgastos.features.gastos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorgastos.features.gastos.domain.entities.Grupo
import com.example.gestorgastos.features.gastos.domain.repositories.GastosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GastosUiState(
    val isLoading: Boolean = false,
    val grupos: List<Grupo> = emptyList(),
    val grupoActual: Grupo? = null,
    val error: String? = null
)

class GastosViewModel(
    private val repository: GastosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GastosUiState())
    val uiState = _uiState.asStateFlow()

    init {
        cargarGrupos()
    }

    fun cargarGrupos() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val grupos = repository.obtenerGrupos()
                _uiState.update { it.copy(isLoading = false, grupos = grupos) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun crearGrupo(nombre: String, personas: List<String>) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val grupo = repository.crearGrupo(nombre, personas)
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        grupos = it.grupos + grupo,
                        grupoActual = grupo
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun seleccionarGrupo(grupo: Grupo) {
        _uiState.update { it.copy(grupoActual = grupo) }
    }

    fun agregarGasto(persona: String, monto: Double, descripcion: String, tipo: String) {
        val grupoId = _uiState.value.grupoActual?.id ?: return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val grupoActualizado = repository.agregarGasto(grupoId, persona, monto, descripcion, tipo)
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        grupoActual = grupoActualizado,
                        grupos = state.grupos.map { if (it.id == grupoId) grupoActualizado else it }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun eliminarGrupo(grupoId: Int) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                repository.eliminarGrupo(grupoId)
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        grupos = state.grupos.filter { it.id != grupoId },
                        grupoActual = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun eliminarGasto(gastoId: Int) {
        val grupoId = _uiState.value.grupoActual?.id ?: return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val grupoActualizado = repository.eliminarGasto(grupoId, gastoId)
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        grupoActual = grupoActualizado,
                        grupos = state.grupos.map { if (it.id == grupoId) grupoActualizado else it }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun limpiarError() {
        _uiState.update { it.copy(error = null) }
    }
}
