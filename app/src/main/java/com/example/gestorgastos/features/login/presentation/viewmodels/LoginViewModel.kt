package com.example.gestorgastos.features.login.presentation.viewmodels

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
    private val sesionDao: SesionDao
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

                if (nombre.isBlank() || password.isBlank()) {
                    _uiState.update { it.copy(isLoading = false, error = "Usuario y contraseña requeridos") }
                    return@launch
                }

                val result = loginUseCase(nombre, password)
                if (result.isSuccess) {
                    rotationManager.enableAutoRotation()
                    activityManager.enableRotation()

                    val userId = tokenManager.getUserId()
                    val token = tokenManager.getToken() ?: ""
                    val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                    // Guardar/actualizar usuario local
                    val existente = usuarioDao.getUsuarioByUsername(nombre)
                    val localId = if (existente != null) {
                        existente.id
                    } else {
                        val usuario = UsuarioEntity(
                            id = userId,
                            username = nombre,
                            email = "",
                            fechaRegistro = fecha
                        )
                        usuarioDao.insertUsuario(usuario).toInt()
                    }

                    sesionDao.cerrarTodasLasSesiones()
                    sesionDao.insertSesion(
                        SesionEntity(
                            usuarioId = localId,
                            username = nombre,
                            token = token,
                            fechaLogin = fecha,
                            activa = true
                        )
                    )
                    usuarioDao.actualizarUltimoLogin(localId, fecha)
                    Log.d(TAG, "LOGIN - Sesión guardada en SQLite: $nombre")

                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Credenciales incorrectas")
                    }
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
