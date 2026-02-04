package com.example.gestorgastos.features.gastos.data.repositories

import com.example.gestorgastos.core.network.GastosApi
import com.example.gestorgastos.features.gastos.data.datasources.remote.mapper.toDomain
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.GastoRequest
import com.example.gestorgastos.features.gastos.domain.entities.Gasto
import com.example.gestorgastos.features.gastos.domain.entities.ResumenGastos
import com.example.gestorgastos.features.gastos.domain.repositories.GastosRepository

class GastosRepositoryImpl(
    private val api: GastosApi
) : GastosRepository {

    override suspend fun crearGasto(monto: Double, descripcion: String, quienPago: String): Gasto {
        val request = GastoRequest(monto, descripcion, quienPago)
        return api.crearGasto(request).toDomain()
    }

    override suspend fun obtenerGastos(): List<Gasto> {
        return api.obtenerGastos().map { it.toDomain() }
    }

    override suspend fun obtenerResumen(): ResumenGastos {
        return api.obtenerResumen().toDomain()
    }

    override suspend fun eliminarGasto(id: Int) {
        api.eliminarGasto(id)
    }
}
