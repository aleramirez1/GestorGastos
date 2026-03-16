package com.example.gestorgastos.features.registro.data.datasources.remote

import com.example.gestorgastos.core.network.GastosApi
import com.example.gestorgastos.features.registro.data.datasources.remote.model.RegistroRequest
import com.example.gestorgastos.features.registro.data.datasources.remote.model.RegistroResponse
import javax.inject.Inject

class RegistroRemoteDataSource @Inject constructor(
    private val api: GastosApi
) {
    suspend fun registro(nombre: String, email: String, password: String): RegistroResponse {
        return api.registro(RegistroRequest(nombre, email, password))
    }
}
