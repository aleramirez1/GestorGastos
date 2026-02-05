package com.example.gestorgastos.features.gastos.domain.usecases

import com.example.gestorgastos.features.gastos.domain.entities.Usuario
import com.example.gestorgastos.features.gastos.domain.repositories.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(nombre: String, password: String): Result<Usuario> {
        return try {
            if (nombre.isBlank()) {
                return Result.failure(Exception("El nombre es requerido"))
            }
            if (password.isBlank()) {
                return Result.failure(Exception("La password es requerida"))
            }
            val result = repository.login(nombre, password)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
