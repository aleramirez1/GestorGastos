package com.example.gestorgastos.features.gastos.domain.repositories

import com.example.gestorgastos.features.gastos.domain.entities.Gasto
import com.example.gestorgastos.features.gastos.domain.entities.ResumenGastos

interface GastosRepository {
    suspend fun crearGasto(monto: Double, descripcion: String, quienPago: String): Gasto
    suspend fun obtenerGastos(): List<Gasto>
    suspend fun obtenerResumen(): ResumenGastos
    suspend fun eliminarGasto(id: Int)
}
