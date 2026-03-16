package com.example.gestorgastos.features.login.presentation.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorgastos.core.database.dao.SesionDao
import com.example.gestorgastos.core.database.dao.UsuarioDao
import com.example.gestorgastos.core.database.entities.SesionEntity
import com.example.gestorgastos.core.database.entities.UsuarioEntity
import com.example.gestorgastos.core.hardware.domain.ActivityManager
import com.example.gestorgastos.core.hardware.domain.RotationManager
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import com.example.gestorgastos.features.login.domain.usecases.LoginUseCase
import com.example.gestorgastos.features.login.presentation.screens.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager,
    private val rotationManager: RotationManager,
    private val activityManager: ActivityManager,
    private val usuarioDao: UsuarioDao,
    private val sesionDao: SesionDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        private const val TAG = "ROOM_SQLITE"
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun login(nombre: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                if (nombre.isNotBlank() && password.isNotBlank()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!Settings.System.canWrite(context)) {
                            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                            intent.data = Uri.parse("package:${context.packageName}")
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                            _uiState.update { it.copy(isLoading = false, error = "Por favor, concede permiso para modificar configuraciones del sistema") }
                            return@launch
                        }
                    }
                    
                    rotationManager.enableAutoRotation()
                    activityManager.enableRotation()
                    
                    var usuario = usuarioDao.getUsuarioByUsername(nombre)
                    if (usuario == null) {
                        val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                        val nuevoUsuario = UsuarioEntity(
                            username = nombre,
                            email = "$nombre@local.com",
                            passwordHash = password.hashCode().toString(),
                            fechaRegistro = fecha
                        )
                        val id = usuarioDao.insertUsuario(nuevoUsuario)
                        usuario = nuevoUsuario.copy(id = id.toInt())
                        Log.d(TAG, "LOGIN - Nuevo usuario creado en SQLite: $nombre con ID $id")
                    }
                    
                    val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                    val token = "offline-token-${System.currentTimeMillis()}"
                    
                    sesionDao.cerrarTodasLasSesiones()
                    val sesion = SesionEntity(
                        usuarioId = usuario.id,
                        username = nombre,
                        token = token,
                        fechaLogin = fecha,
                        activa = true
                    )
                    sesionDao.insertSesion(sesion)
                    usuarioDao.actualizarUltimoLogin(usuario.id, fecha)
                    Log.d(TAG, "LOGIN - Sesión guardada en SQLite: $nombre, fecha: $fecha")
                    
                    tokenManager.saveUserId(usuario.id)
                    tokenManager.saveUserName(nombre)
                    tokenManager.saveToken(token)
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Usuario y contraseña requeridos") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error: ${e.message}") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
