package com.example.gestorgastos.features.login.data.repositories

import com.example.gestorgastos.core.network.GastosApi
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import com.example.gestorgastos.features.login.data.datasources.remote.model.LoginRequest
import com.example.gestorgastos.features.login.data.datasources.remote.model.PerfilUpdateRequest
import com.example.gestorgastos.features.login.domain.repositories.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val api: GastosApi,
    private val tokenManager: TokenManager
) : LoginRepository {

    override suspend fun login(nombre: String, password: String): Result<Unit> {
        return try {
            val response = api.login(LoginRequest(nombre, password))
            tokenManager.saveToken(response.token)
            tokenManager.saveUserName(response.nombre)
            tokenManager.saveUserId(response.id)
            response.fotoPerfil?.let { tokenManager.saveUserProfilePic(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun actualizarPerfil(usuarioId: Int, nombre: String, fotoPerfil: String?): Result<Unit> {
        return try {
            val response = api.actualizarPerfil(usuarioId, PerfilUpdateRequest(nombre, fotoPerfil))
            // Actualizamos localmente con lo que nos diga el servidor (que debería ser lo mismo o la URL final)
            tokenManager.saveUserName(response.nombre)
            response.fotoPerfil?.let { tokenManager.saveUserProfilePic(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
