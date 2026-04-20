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
    private val flashlightManager: FlashlightManager,
    private val notificationManager: com.example.gestorgastos.core.notifications.domain.NotificationManager,
    private val invitacionDao: com.example.gestorgastos.core.database.dao.InvitacionDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(GruposUiState())
    val uiState = _uiState.asStateFlow()

    init {
        cargarGrupos()
        cargarDatosUsuario()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    private fun cargarDatosUsuario() {
        val uri = tokenManager.getUserProfilePic()
        val nombre = tokenManager.getUserName()
        _uiState.update { it.copy(fotoPerfil = uri, nombreUsuario = nombre) }
    }

    fun procesarCodigoInvitacionPendiente(context: android.content.Context, codigoManual: String? = null) {
        val prefs = context.getSharedPreferences("invitaciones", android.content.Context.MODE_PRIVATE)
        val codigoAceptado = codigoManual ?: prefs.getString("codigo_aceptado", null) ?: return
        val nombreNuevo = tokenManager.getUserName() ?: "Nuevo Miembro"
        
        if (codigoManual == null) {
            prefs.edit().remove("codigo_aceptado").remove("nombre_nuevo_usuario").apply()
        }

        val usuarioId = tokenManager.getUserId()

        val db = com.google.firebase.database.FirebaseDatabase
            .getInstance("https://tests-abe52-default-rtdb.firebaseio.com/")
            .getReference("invitaciones")

        db.child(codigoAceptado).get()
            .addOnSuccessListener { snapshot ->
                val invitacion = snapshot.value as? Map<*, *>
                if (invitacion != null) {
                    val grupoNombre = invitacion["grupo_nombre"] as? String ?: "Grupo"
                    val gId = when (val raw = invitacion["grupo_id"]) {
                        is Long -> raw.toInt()
                        is Int -> raw
                        is Double -> raw.toInt()
                        else -> 0
                    }
                    guardarGrupoLocalYCargar(gId, grupoNombre, nombreNuevo, usuarioId)
                } else {
                    _uiState.update { it.copy(error = "Código no encontrado") }
                }
            }
            .addOnFailureListener { e ->
                _uiState.update { it.copy(error = "Error: ${e.message}") }
            }
    }

    private fun guardarGrupoLocalYCargar(grupoId: Int, grupoNombre: String, nombreNuevo: String, usuarioId: Int) {
        val grupoLocal = Grupo(
            id = grupoId,
            nombre = grupoNombre,
            usuarioId = usuarioId,
            fechaCreacion = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
            personas = listOf(nombreNuevo),
            gastos = emptyList()
        )
        viewModelScope.launch {
            repository.guardarGrupoLocal(grupoLocal)
            repository.agregarPersona(grupoId, nombreNuevo)
            cargarGrupos()
            
            try {
                notificationManager.showLocalNotification(
                    title = "¡Bienvenido!",
                    message = "Te uniste al grupo $grupoNombre",
                    channelId = "grupos"
                )
            } catch (_: Exception) {}
        }
    }

    fun obtenerNombreUsuario(): String = tokenManager.getUserName() ?: ""

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

    fun crearGrupo(
        nombre: String, 
        personas: List<String>, 
        fotoTicketUri: String? = null, 
        ganadorRuleta: String? = null,
        isAhorro: Boolean = false,
        metaAhorro: Double = 0.0,
        fechaLimite: String? = null
    ) {
        val usuarioId = tokenManager.getUserId()
        if (usuarioId == 0) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val nuevoGrupoBase = repository.crearGrupo(nombre, personas, usuarioId, isAhorro, metaAhorro, fechaLimite)
                val nuevoGrupo = nuevoGrupoBase.copy(
                    fotoTicketUri = fotoTicketUri,
                    ganadorRuleta = ganadorRuleta
                )
                
                repository.actualizarGrupoLocal(nuevoGrupo)

                alertManager.vibrate(500)
                repeat(3) {
                    flashlightManager.turnOn()
                    delay(200)
                    flashlightManager.turnOff()
                    delay(200)
                }

                notificationManager.showLocalNotification(
                    title = "Grupo Creado",
                    message = "El grupo '$nombre' ha sido creado",
                    channelId = "grupos"
                )

                _uiState.update {
                    it.copy(isLoading = false, grupos = it.grupos + nuevoGrupo, grupoActual = nuevoGrupo)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error: ${e.message}") }
            }
        }
    }

    fun finalizarMeta(grupoId: Int) {
        _uiState.update { it.copy(isLoading = true) }
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
                _uiState.update { it.copy(isLoading = false, error = "Error: ${e.message}") }
            }
        }
    }

    fun seleccionarGrupo(grupo: Grupo) {
        _uiState.update { it.copy(grupoActual = grupo) }
    }

    fun agregarGasto(persona: String, monto: Double, descripcion: String, tipo: String, comprobanteUri: String? = null) {
        val grupoActual = _uiState.value.grupoActual ?: return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val grupoActualizado = repository.agregarGasto(grupoActual.id, persona, monto, descripcion, tipo, comprobanteUri)
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        grupoActual = grupoActualizado,
                        grupos = state.grupos.map { if (it.id == grupoActual.id) grupoActualizado else it }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error: ${e.message}") }
            }
        }
    }

    fun actualizarGanadorRuletaGrupo(grupoId: Int, ganador: String) {
        viewModelScope.launch {
            try {
                val grupoExistente = _uiState.value.grupos.find { it.id == grupoId }
                if (grupoExistente != null) {
                    val grupoActualizado = grupoExistente.copy(ganadorRuleta = ganador)
                    repository.actualizarGrupo(grupoActualizado)
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

    fun enviarInvitacionEmail(context: android.content.Context, grupoNombre: String, email: String, grupoIdOverride: Int = -1) {
        val grupoActual = _uiState.value.grupoActual
        val grupoId = if (grupoIdOverride != -1) grupoIdOverride else (grupoActual?.id ?: 0)
        val codigoInvitacion = "GG${grupoId}-${(1000..9999).random()}"

        viewModelScope.launch {
            try {
                val usuarioNombre = tokenManager.getUserName() ?: "Miembro"

                val entity = com.example.gestorgastos.core.database.entities.InvitacionEntity(
                    grupoId = grupoId,
                    grupoNombre = grupoNombre,
                    invitadoPor = usuarioNombre,
                    invitadoTelefono = "",
                    invitadoNombre = "",
                    mensaje = codigoInvitacion
                )
                invitacionDao.insertInvitacion(entity)

                val invitacionData = mapOf(
                    "codigo_invitacion" to codigoInvitacion,
                    "estado" to "pendiente",
                    "grupo_id" to grupoId,
                    "grupo_nombre" to grupoNombre,
                    "invitado_email" to email,
                    "invitado_por" to usuarioNombre,
                    "timestamp" to System.currentTimeMillis(),
                    "tipo" to "invitacion_email"
                )

                com.google.firebase.database.FirebaseDatabase
                    .getInstance("https://tests-abe52-default-rtdb.firebaseio.com/")
                    .getReference("invitaciones")
                    .child(codigoInvitacion)
                    .setValue(invitacionData)
                    .addOnSuccessListener {
                        notificationManager.showLocalNotification(
                            title = "Invitación enviada",
                            message = "Se envió invitación a $email",
                            channelId = "grupos"
                        )
                    }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }
}
