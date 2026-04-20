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
    }

    fun procesarCodigoInvitacionPendiente(context: android.content.Context) {
        val prefs = context.getSharedPreferences("invitaciones", android.content.Context.MODE_PRIVATE)
        val codigoAceptado = prefs.getString("codigo_aceptado", null) ?: return
        val nombreNuevo = prefs.getString("nombre_nuevo_usuario", null) ?: return
        prefs.edit().remove("codigo_aceptado").remove("nombre_nuevo_usuario").apply()
        
        viewModelScope.launch {
            try {
                android.util.Log.d("GruposVM", "Procesando código: '$codigoAceptado' para usuario: '$nombreNuevo'")
                
                val grupoId = extraerGrupoIdDeCodigo(codigoAceptado)
                
                if (grupoId == null) {
                    android.util.Log.e("GruposVM", "Formato de código no reconocido: $codigoAceptado")
                    _uiState.update { it.copy(error = "No se pudo procesar el código de invitación") }
                    kotlinx.coroutines.delay(3000)
                    _uiState.update { it.copy(error = null) }
                    return@launch
                }
                
                android.util.Log.d("GruposVM", "Agregando '$nombreNuevo' al grupo $grupoId")
                val grupoActualizado = repository.agregarPersona(grupoId, nombreNuevo)
                
                notificationManager.showLocalNotification(
                    title = "¡Bienvenido!",
                    message = "Te uniste al grupo ${grupoActualizado.nombre}",
                    channelId = "grupos"
                )
                
                _uiState.update { it.copy(grupos = listOf(grupoActualizado), isLoading = false, error = null) }
                
            } catch (e: Exception) {
                android.util.Log.e("GruposVM", "Error al procesar código: ${e.message}")
                _uiState.update { it.copy(error = "Error al unirse: ${e.message}") }
                kotlinx.coroutines.delay(3000)
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun extraerGrupoIdDeCodigo(codigo: String): Int? {
        val limpio = codigo.trim().uppercase()
        
        // Formato GG{id}-{random}, ej: GG34-5678
        if (limpio.startsWith("GG")) {
            val sinPrefijo = limpio.removePrefix("GG")
            val id = sinPrefijo.substringBefore("-").toIntOrNull()
            if (id != null && id > 0) return id
        }
        
        // Formato GG-{nombre}-{random}, ej: GG-GRUP-1234 (formato viejo)
        // En este caso no podemos extraer el grupoId, retornamos null
        return null
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

                notificationManager.showLocalNotification(
                    title = "Grupo Creado",
                    message = "El grupo '$nombre' ha sido creado exitosamente",
                    channelId = "grupos"
                )

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

    fun enviarInvitacionEmail(context: android.content.Context, grupoNombre: String, email: String, grupoIdOverride: Int = -1) {
        val grupoActual = _uiState.value.grupoActual
        val grupoId = if (grupoIdOverride != -1) grupoIdOverride else (grupoActual?.id ?: 0)
        val codigoInvitacion = "GG${grupoId}-${(1000..9999).random()}"

        viewModelScope.launch {
            try {
                val usuarioNombre = tokenManager.getUserName() ?: "alguien"

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
                        android.util.Log.d("GruposVM", "Invitación guardada en Firebase para $email")
                        notificationManager.showLocalNotification(
                            title = "Invitación enviada",
                            message = "Se envió invitación a $email",
                            channelId = "grupos"
                        )
                    }
                    .addOnFailureListener { e ->
                        _uiState.update { it.copy(error = "Error al enviar: ${e.message}") }
                    }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }

    fun unirseAlGrupoPorCodigo(codigo: String, nombreUsuario: String) {
        viewModelScope.launch {
            try {
                val invitaciones = repository.obtenerGruposLocales(0)
                val usuarioId = tokenManager.getUserId()
                val grupos = repository.obtenerGrupos(usuarioId)
                
                grupos.forEach { grupo ->
                    val grupoActualizado = repository.agregarPersona(grupo.id, nombreUsuario)
                    if (grupoActualizado != null) {
                        _uiState.update { state ->
                            state.copy(
                                grupos = state.grupos.map { if (it.id == grupo.id) grupoActualizado else it },
                                error = null
                            )
                        }
                        notificationManager.showLocalNotification(
                            title = "Te uniste al grupo",
                            message = "Ahora eres parte de ${grupo.nombre}",
                            channelId = "grupos"
                        )
                        return@launch
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Código inválido o expirado") }
            }
        }
    }

    private fun validarTelefono(telefono: String): Boolean {
        val limpio = telefono.replace(Regex("[^0-9+]"), "")
        return limpio.length >= 10
    }

}
