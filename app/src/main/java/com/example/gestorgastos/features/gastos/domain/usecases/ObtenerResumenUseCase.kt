package com.example.gestorgastos.features.gastos.domain.usecases

import com.example.gestorgastos.features.gastos.domain.entities.ResumenGastos
import com.example.gestorgastos.features.gastos.domain.repositories.GastosRepository

class ObtenerResumenUseCase(
    private val repository: GastosRepository
) {
    suspend operator fun invoke(): Result<ResumenGastos> {
        return try {
            val result = repository.obtenerResumen()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
