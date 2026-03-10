package com.example.gestorgastos.features.registro.data.repositories

import com.example.gestorgastos.core.network.GastosApi
import com.example.gestorgastos.features.registro.data.datasources.remote.model.RegistroRequest
import com.example.gestorgastos.features.registro.domain.repositories.RegistroRepository
import javax.inject.Inject

class RegistroRepositoryImpl @Inject constructor(private val api: GastosApi) : RegistroRepository {

    override suspend fun registro(nombre: String, email: String, password: String): Result<Unit> {
        return try {
            api.registro(RegistroRequest(nombre, email, password))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
