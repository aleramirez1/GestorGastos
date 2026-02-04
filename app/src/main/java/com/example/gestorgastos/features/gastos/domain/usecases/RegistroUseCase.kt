package com.example.gestorgastos.features.gastos.domain.usecases

import com.example.gestorgastos.features.gastos.domain.entities.Usuario
import com.example.gestorgastos.features.gastos.domain.repositories.AuthRepository

class RegistroUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(nombre: String, email: String, password: String): Result<Usuario> {
        return try {
            if (nombre.isBlank()) {
                return Result.failure(Exception("El nombre es requerido"))
            }
            if (email.isBlank()) {
                return Result.failure(Exception("El email es requerido"))
            }
            if (password.length < 4) {
                return Result.failure(Exception("La password debe tener al menos 4 caracteres"))
            }
            val result = repository.registro(nombre, email, password)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
