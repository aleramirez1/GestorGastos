package com.example.gestorgastos.features.login.presentation.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorgastos.core.hardware.domain.ActivityManager
import com.example.gestorgastos.core.hardware.domain.RotationManager
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import com.example.gestorgastos.features.login.domain.usecases.LoginUseCase
import com.example.gestorgastos.features.login.presentation.screens.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager,
    private val rotationManager: RotationManager,
    private val activityManager: ActivityManager,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun login(nombre: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                // Permitir acceso con cualquier usuario y contraseña (modo offline)
                if (nombre.isNotBlank() && password.isNotBlank()) {
                    // Verificar y solicitar permiso para modificar configuraciones del sistema
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!Settings.System.canWrite(context)) {
                            // Solicitar permiso
                            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                            intent.data = Uri.parse("package:${context.packageName}")
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                            _uiState.update { it.copy(isLoading = false, error = "Por favor, concede permiso para modificar configuraciones del sistema") }
                            return@launch
                        }
                    }
                    
                    // Activar rotación automática en la configuración del sistema
                    rotationManager.enableAutoRotation()
                    
                    // Permitir que la app rote
                    activityManager.enableRotation()
                    
                    // Guardar datos localmente
                    tokenManager.saveUserId(1)
                    tokenManager.saveUserName(nombre)
                    tokenManager.saveToken("offline-token-${System.currentTimeMillis()}")
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    // Si están vacíos, mostrar error
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
