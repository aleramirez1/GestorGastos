package com.example.gestorgastos.features.gastos.domain.usecases

import com.example.gestorgastos.features.gastos.domain.entities.Usuario
import com.example.gestorgastos.features.gastos.domain.repositories.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Usuario> {
        return try {
            if (email.isBlank()) {
                return Result.failure(Exception("El email es requerido"))
            }
            if (password.isBlank()) {
                return Result.failure(Exception("La password es requerida"))
            }
            val result = repository.login(email, password)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
