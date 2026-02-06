package com.example.gestorgastos.features.registro.domain.repositories

interface RegistroRepository {
    suspend fun registro(nombre: String, email: String, password: String): Result<Unit>
}
