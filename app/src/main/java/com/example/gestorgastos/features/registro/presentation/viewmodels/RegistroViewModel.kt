package com.example.gestorgastos.features.registro.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorgastos.core.database.dao.UsuarioDao
import com.example.gestorgastos.core.database.entities.UsuarioEntity
import com.example.gestorgastos.features.registro.domain.usecases.RegistroUseCase
import com.example.gestorgastos.features.registro.presentation.screens.RegistroUiState
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
class RegistroViewModel @Inject constructor(
    private val registroUseCase: RegistroUseCase,
    private val usuarioDao: UsuarioDao
) : ViewModel() {

    companion object {
        private const val TAG = "ROOM_SQLITE"
    }

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState = _uiState.asStateFlow()

    fun registro(nombre: String, email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "Todos los campos son requeridos") }
            return
        }

        if (!email.contains("@")) {
            _uiState.update { it.copy(isLoading = false, error = "Correo inválido") }
            return
        }

        if (password.length < 4) {
            _uiState.update { it.copy(isLoading = false, error = "La contraseña debe tener mínimo 4 caracteres") }
            return
        }

        viewModelScope.launch {
            val result = registroUseCase(nombre, email, password)
            if (result.isSuccess) {
                val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val usuario = UsuarioEntity(
                    username = nombre,
                    email = email,
                    fechaRegistro = fecha
                )
                val id = usuarioDao.insertUsuario(usuario)
                Log.d(TAG, "REGISTRO - Usuario guardado en SQLite: $nombre, ID: $id")
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Error al registrar"
                _uiState.update { it.copy(isLoading = false, error = msg) }
            }
        }
    }

    fun resetState() {
        _uiState.update { RegistroUiState() }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
