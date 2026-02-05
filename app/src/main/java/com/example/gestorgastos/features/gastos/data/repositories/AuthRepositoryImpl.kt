package com.example.gestorgastos.features.gastos.data.repositories

import com.example.gestorgastos.core.network.GastosApi
import com.example.gestorgastos.features.gastos.data.datasources.local.TokenManager
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.LoginRequest
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.RegistroRequest
import com.example.gestorgastos.features.gastos.domain.entities.Usuario
import com.example.gestorgastos.features.gastos.domain.repositories.AuthRepository

class AuthRepositoryImpl(
    private val api: GastosApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(nombre: String, password: String): Usuario {
        val response = api.login(LoginRequest(nombre, password))
        tokenManager.saveToken(response.token)
        tokenManager.saveUserName(response.nombre)
        tokenManager.saveUserId(response.id)
        return Usuario(response.id, response.nombre, response.email, response.token)
    }

    override suspend fun registro(nombre: String, email: String, password: String): Usuario {
        val response = api.registro(RegistroRequest(nombre, email, password))
        return Usuario(response.id, response.nombre, response.email, response.token)
    }

    override fun logout() {
        tokenManager.clearAll()
    }

    override fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }
}
