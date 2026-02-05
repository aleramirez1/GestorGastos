package com.example.gestorgastos.features.gastos.data.datasources.remote.model

data class GastoCreateRequest(
    val monto: Double,
    val descripcion: String,
    val persona: String,
    val tipo: String
)