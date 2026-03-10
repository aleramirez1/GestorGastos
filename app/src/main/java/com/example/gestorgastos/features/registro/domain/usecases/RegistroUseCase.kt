package com.example.gestorgastos.features.registro.domain.usecases

import com.example.gestorgastos.features.registro.domain.repositories.RegistroRepository
import javax.inject.Inject

class RegistroUseCase @Inject constructor(
    private val repository: RegistroRepository
) {
    suspend operator fun invoke(nombre: String, email: String, password: String): Result<Unit> {
        return repository.registro(nombre, email, password)
    }
}