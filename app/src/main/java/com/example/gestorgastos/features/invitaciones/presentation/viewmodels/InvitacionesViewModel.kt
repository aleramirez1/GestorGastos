package com.example.gestorgastos.features.invitaciones.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorgastos.features.invitaciones.domain.entities.InvitacionGrupo
import com.example.gestorgastos.features.invitaciones.domain.usecases.ObtenerInvitacionesUseCase
import com.example.gestorgastos.features.invitaciones.domain.usecases.ResponderInvitacionUseCase
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InvitacionesUiState(
    val invitaciones: List<InvitacionGrupo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null
)

@HiltViewModel
class InvitacionesViewModel @Inject constructor(
    private val obtenerInvitacionesUseCase: ObtenerInvitacionesUseCase,
    private val responderInvitacionUseCase: ResponderInvitacionUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvitacionesUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarInvitaciones(telefono: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                obtenerInvitacionesUseCase(telefono).collect { invitaciones ->
                    _uiState.update {
                        it.copy(
                            invitaciones = invitaciones,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar invitaciones: ${e.message}"
                    )
                }
            }
        }
    }

    fun aceptarInvitacion(invitacionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val usuarioId = tokenManager.getUserId()
            val result = responderInvitacionUseCase.aceptar(invitacionId, usuarioId)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, mensaje = "Te uniste al grupo exitosamente") }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = "Error al aceptar: ${error.message}") }
                }
            )
        }
    }

    fun aceptarInvitacionConNombre(invitacionId: String, nombre: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val usuarioId = tokenManager.getUserId()
            val result = responderInvitacionUseCase.aceptarConNombre(invitacionId, usuarioId, nombre)
            result.fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            mensaje = "¡Te uniste al grupo como $nombre!",
                            invitaciones = state.invitaciones.filter { it.id != invitacionId }
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = "Error al unirse: ${error.message}") }
                }
            )
        }
    }

    fun buscarInvitacionPorCodigo(codigo: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = obtenerInvitacionesUseCase.buscarPorCodigo(codigo)
            result.fold(
                onSuccess = { invitacion ->
                    _uiState.update { state ->
                        val yaExiste = state.invitaciones.any { it.id == invitacion.id }
                        state.copy(
                            isLoading = false,
                            invitaciones = if (yaExiste) state.invitaciones else state.invitaciones + invitacion
                        )
                    }
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false, error = "Código inválido o no encontrado") }
                }
            )
        }
    }

    fun rechazarInvitacion(invitacionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = responderInvitacionUseCase.rechazar(invitacionId)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            mensaje = "Invitación rechazada"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Error al rechazar: ${error.message}"
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }
}
