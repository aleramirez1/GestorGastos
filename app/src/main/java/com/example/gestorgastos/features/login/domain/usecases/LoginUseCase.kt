package com.example.gestorgastos.features.login.domain.usecases

import com.example.gestorgastos.features.login.domain.repositories.LoginRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: LoginRepository) {
    suspend operator fun invoke(nombre: String, password: String): Result<Unit> {
        return repository.login(nombre, password)
    }
}
