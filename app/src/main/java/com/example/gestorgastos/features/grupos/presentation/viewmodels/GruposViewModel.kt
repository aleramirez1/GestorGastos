package com.example.gestorgastos.features.grupos.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorgastos.core.hardware.domain.AlertManager
import com.example.gestorgastos.core.hardware.domain.CameraManager
import com.example.gestorgastos.core.hardware.domain.FlashlightManager
import com.example.gestorgastos.features.grupos.domain.entities.Grupo
import com.example.gestorgastos.features.grupos.domain.repositories.GruposRepository
import com.example.gestorgastos.features.grupos.presentation.screens.GruposUiState
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GruposViewModel @Inject constructor(
    private val repository: GruposRepository,
    private val tokenManager: TokenManager,
    private val cameraManager: CameraManager,
    private val alertManager: AlertManager,
    private val flashlightManager: FlashlightManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(GruposUiState())
    val uiState = _uiState.asStateFlow()

    init {
        cargarGrupos()
    }

    fun cargarGrupos() {
        val usuarioId = tokenManager.getUserId()
        if (usuarioId == 0) {
            _uiState.update { it.copy(isLoading = false, grupos = emptyList()) }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val grupos = repository.obtenerGrupos(usuarioId)
                _uiState.update { it.copy(isLoading = false, grupos = grupos) }
            } catch (e: Exception) {
                val locales = repository.obtenerGruposLocales(usuarioId)
                _uiState.update { it.copy(isLoading = false, grupos = locales) }
            }
        }
    }

    fun crearGrupo(nombre: String, personas: List<String>, fotoTicketUri: String? = null, ganadorRuleta: String? = null) {
        val usuarioId = tokenManager.getUserId()
        if (usuarioId == 0) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val nuevoGrupo = repository.crearGrupo(nombre, personas, usuarioId)

                alertManager.vibrate(500)
                repeat(3) {
                    flashlightManager.turnOn()
                    delay(200)
                    flashlightManager.turnOff()
                    delay(200)
                }

                _uiState.update {
                    it.copy(isLoading = false, grupos = it.grupos + nuevoGrupo, grupoActual = nuevoGrupo)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al crear grupo: ${e.message}") }
            }
        }
    }
    
    fun setGanadorRuleta(ganador: String) {
        _uiState.update { it.copy(ganadorRuleta = ganador) }
    }
    
    fun actualizarGanadorRuletaGrupo(grupoId: Int, ganador: String) {
        viewModelScope.launch {
            try {
                val grupoActualizado = _uiState.value.grupos.find { it.id == grupoId }?.copy(ganadorRuleta = ganador)
                if (grupoActualizado != null) {
                    repository.actualizarGrupoLocal(grupoActualizado)
                    
                    _uiState.update { state ->
                        state.copy(
                            grupos = state.grupos.map { if (it.id == grupoId) grupoActualizado else it },
                            grupoActual = if (state.grupoActual?.id == grupoId) grupoActualizado else state.grupoActual
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al actualizar ganador: ${e.message}") }
            }
        }
    }

    fun seleccionarGrupo(grupo: Grupo) {
        _uiState.update { it.copy(grupoActual = grupo) }
    }

    fun agregarGasto(persona: String, monto: Double, descripcion: String, tipo: String) {
        val grupoActual = _uiState.value.grupoActual ?: return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val grupoActualizado = repository.agregarGasto(grupoActual.id, persona, monto, descripcion, tipo)
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        grupoActual = grupoActualizado,
                        grupos = state.grupos.map { if (it.id == grupoActual.id) grupoActualizado else it }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al agregar gasto: ${e.message}") }
            }
        }
    }

    fun tomarFotoGasto(): Uri? {
        var photoUri: Uri? = null
        viewModelScope.launch {
            val result = cameraManager.takePicture()
            result.onSuccess { uri ->
                photoUri = uri
                _uiState.update { it.copy(fotoGastoUri = uri.toString()) }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
        return photoUri
    }

    fun eliminarGrupo(grupoId: Int) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                repository.eliminarGrupo(grupoId)
                _uiState.update { state ->
                    state.copy(isLoading = false, grupos = state.grupos.filter { it.id != grupoId }, grupoActual = null)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al eliminar grupo: ${e.message}") }
            }
        }
    }

    fun eliminarGasto(gastoId: Int) {
        val grupoActual = _uiState.value.grupoActual ?: return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val grupoActualizado = repository.eliminarGasto(grupoActual.id, gastoId)
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        grupoActual = grupoActualizado,
                        grupos = state.grupos.map { if (it.id == grupoActual.id) grupoActualizado else it }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al eliminar gasto: ${e.message}") }
            }
        }
    }

    fun agregarPersona(persona: String) {
        val grupoActual = _uiState.value.grupoActual ?: return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val grupoActualizado = repository.agregarPersona(grupoActual.id, persona)
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        grupoActual = grupoActualizado,
                        grupos = state.grupos.map { if (it.id == grupoActual.id) grupoActualizado else it }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al agregar persona: ${e.message}") }
            }
        }
    }

    fun eliminarPersona(persona: String) {
        val grupoActual = _uiState.value.grupoActual ?: return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val grupoActualizado = repository.eliminarPersona(grupoActual.id, persona)
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        grupoActual = grupoActualizado,
                        grupos = state.grupos.map { if (it.id == grupoActual.id) grupoActualizado else it }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al eliminar persona: ${e.message}") }
            }
        }
    }

    fun editarGasto(gastoId: Int, nuevoMonto: Double) {
        val grupoActual = _uiState.value.grupoActual ?: return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val grupoActualizado = repository.editarGasto(grupoActual.id, gastoId, nuevoMonto)
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        grupoActual = grupoActualizado,
                        grupos = state.grupos.map { if (it.id == grupoActual.id) grupoActualizado else it }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al editar gasto: ${e.message}") }
            }
        }
    }
}
