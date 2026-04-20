package com.example.gestorgastos.features.login.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import com.example.gestorgastos.features.login.domain.repositories.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PerfilUiState(
    val nombre: String = "",
    val fotoUri: String? = null,
    val isLoading: Boolean = false,
    val mensaje: String? = null,
    val error: String? = null
)

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val savedName = tokenManager.getUserName() ?: ""
        val savedPhoto = tokenManager.getUserProfilePic()
        Log.d("PERFIL", "Cargando perfil: $savedName, $savedPhoto")
        _uiState.update { it.copy(
            nombre = savedName,
            fotoUri = savedPhoto
        ) }
    }

    fun updateNombre(nuevoNombre: String) {
        _uiState.update { it.copy(nombre = nuevoNombre) }
    }

    fun updateFoto(uri: String) {
        Log.d("PERFIL", "Foto actualizada localmente: $uri")
        _uiState.update { it.copy(fotoUri = uri) }
    }

    fun guardarCambios() {
        val usuarioId = tokenManager.getUserId()
        Log.d("PERFIL", "Intentando guardar cambios para ID: $usuarioId")
        
        if (usuarioId == 0) {
            _uiState.update { it.copy(error = "Error: ID de usuario no encontrado. Reicia sesión.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                Log.d("PERFIL", "Llamando a repository.actualizarPerfil...")
                val result = repository.actualizarPerfil(
                    usuarioId = usuarioId,
                    nombre = _uiState.value.nombre,
                    fotoPerfil = _uiState.value.fotoUri
                )
                
                result.onSuccess {
                    Log.d("PERFIL", "Éxito en el servidor")
                    tokenManager.saveUserName(_uiState.value.nombre)
                    _uiState.value.fotoUri?.let { tokenManager.saveUserProfilePic(it) }
                    _uiState.update { it.copy(isLoading = false, mensaje = "¡Perfil actualizado con éxito!") }
                }.onFailure { e ->
                    Log.e("PERFIL", "Fallo en el servidor", e)
                    _uiState.update { it.copy(isLoading = false, error = "El servidor no respondió correctamente: ${e.message}") }
                }
            } catch (e: Exception) {
                Log.e("PERFIL", "Excepción inesperada", e)
                _uiState.update { it.copy(isLoading = false, error = "Ocurrió un error inesperado") }
            }
        }
    }
    
    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null, error = null) }
    }
}
