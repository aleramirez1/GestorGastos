package com.example.gestorgastos.features.login.data.datasources.remote

import com.example.gestorgastos.core.network.GastosApi
import com.example.gestorgastos.features.login.data.datasources.remote.model.LoginRequest
import com.example.gestorgastos.features.login.data.datasources.remote.model.LoginResponse
import javax.inject.Inject

class LoginRemoteDataSource @Inject constructor(
    private val api: GastosApi
) {
    suspend fun login(nombre: String, password: String): LoginResponse {
        return api.login(LoginRequest(nombre, password))
    }
}
