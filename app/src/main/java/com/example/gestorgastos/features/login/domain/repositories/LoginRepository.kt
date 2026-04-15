package com.example.gestorgastos.features.login.domain.repositories

interface LoginRepository {
    suspend fun login(nombre: String, password: String): Result<Unit>
    suspend fun actualizarPerfil(usuarioId: Int, nombre: String, fotoPerfil: String?): Result<Unit>
}
