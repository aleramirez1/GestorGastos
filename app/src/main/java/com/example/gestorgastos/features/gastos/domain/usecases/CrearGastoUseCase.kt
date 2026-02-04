package com.example.gestorgastos.features.gastos.domain.usecases

import com.example.gestorgastos.features.gastos.domain.entities.Gasto
import com.example.gestorgastos.features.gastos.domain.repositories.GastosRepository

class CrearGastoUseCase(
    private val repository: GastosRepository
) {
    suspend operator fun invoke(monto: Double, descripcion: String, quienPago: String, tipo: String): Result<Gasto> {
        return try {
            if (monto <= 0) {
                return Result.failure(Exception("El monto debe ser mayor a 0"))
            }
            if (descripcion.isBlank()) {
                return Result.failure(Exception("La descripcion no puede estar vacia"))
            }
            if (quienPago.isBlank()) {
                return Result.failure(Exception("Debe indicar a quien"))
            }
            val result = repository.crearGasto(monto, descripcion, quienPago, tipo)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
