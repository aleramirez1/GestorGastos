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

        val usuarioId = tokenManager.getUserId()
        android.util.Log.d("GruposVM", "Buscando código: '$codigoAceptado'")

        val db = com.google.firebase.database.FirebaseDatabase
            .getInstance("https://tests-abe52-default-rtdb.firebaseio.com/")
            .getReference("invitaciones")

        // Buscar directamente por el código como clave del nodo
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
                    android.util.Log.d("GruposVM", "Encontrado: grupo=$grupoNombre id=$gId")
                    guardarGrupoLocalYCargar(gId, grupoNombre, nombreNuevo, usuarioId)
                } else {
                    // No encontrado como clave directa, buscar por campo codigo_invitacion
                    db.orderByChild("codigo_invitacion").equalTo(codigoAceptado).get()
                        .addOnSuccessListener { snap ->
                            val inv = snap.children.firstOrNull()?.value as? Map<*, *>
                            if (inv != null) {
                                val grupoNombre = inv["grupo_nombre"] as? String ?: "Grupo"
                                val gId = when (val raw = inv["grupo_id"]) {
                                    is Long -> raw.toInt()
                                    is Int -> raw
                                    is Double -> raw.toInt()
                                    else -> 0
                                }
                                guardarGrupoLocalYCargar(gId, grupoNombre, nombreNuevo, usuarioId)
                            } else {
                                _uiState.update { it.copy(error = "Código '$codigoAceptado' no encontrado") }
                            }
                        }
                        .addOnFailureListener { e ->
                            _uiState.update { it.copy(error = "Error: ${e.message}") }
                        }
                }
            }
            .addOnFailureListener { e ->
                _uiState.update { it.copy(error = "Error de conexión: ${e.message}") }
            }
    }

    private fun guardarGrupoLocalYCargar(grupoId: Int, grupoNombre: String, nombreNuevo: String, usuarioId: Int) {
        val grupoLocal = com.example.gestorgastos.features.grupos.domain.entities.Grupo(
            id = grupoId,
            nombre = grupoNombre,
            usuarioId = usuarioId,
            fechaCreacion = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
            personas = listOf(nombreNuevo),
            gastos = emptyList()
        )
        viewModelScope.launch {
            repository.guardarGrupoLocal(grupoLocal)
            
            val locales = repository.obtenerGruposLocales(usuarioId)
            _uiState.update { it.copy(grupos = locales, isLoading = false, error = null) }
            
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
                val remotos = repository.obtenerGrupos(usuarioId)
                val locales = repository.obtenerGruposLocales(usuarioId)
                val todosIds = remotos.map { it.id }.toSet()
                val soloLocales = locales.filter { it.id !in todosIds }
                val todos = remotos + soloLocales
                _uiState.update { it.copy(isLoading = false, grupos = todos) }
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

                try {
                    alertManager.vibrate(500)
                    repeat(3) {
                        flashlightManager.turnOn()
                        delay(200)
                        flashlightManager.turnOff()
                        delay(200)
                    }
                } catch (_: Exception) {}

                try {
                    notificationManager.showLocalNotification(
                        title = "Grupo Creado",
                        message = "El grupo '$nombre' ha sido creado exitosamente",
                        channelId = "grupos"
                    )
                } catch (_: Exception) {}

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
