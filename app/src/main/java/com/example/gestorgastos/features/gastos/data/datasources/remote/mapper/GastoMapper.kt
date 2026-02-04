package com.example.gestorgastos.features.gastos.data.datasources.remote.mapper

import com.example.gestorgastos.features.gastos.data.datasources.remote.model.DeudaResponse
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.GastoResponse
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.ResumenResponse
import com.example.gestorgastos.features.gastos.domain.entities.Deuda
import com.example.gestorgastos.features.gastos.domain.entities.Gasto
import com.example.gestorgastos.features.gastos.domain.entities.ResumenGastos

fun GastoResponse.toDomain(): Gasto {
    return Gasto(
        id = id,
        monto = monto,
        descripcion = descripcion,
        quienPago = quienPago,
        fecha = fecha
    )
}

fun DeudaResponse.toDomain(): Deuda {
    return Deuda(
        persona = persona,
        debe = debe,
        descripcion = descripcion
    )
}

fun ResumenResponse.toDomain(): ResumenGastos {
    return ResumenGastos(
        totalGastado = totalGastado,
        montoPorPersona = montoPorPersona,
        numPersonas = numPersonas,
        personas = personas,
        deudas = deudas.map { it.toDomain() }
    )
}
