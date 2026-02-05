package com.example.gestorgastos.features.gastos.data.datasources.remote.model

data class GrupoResponse(
    val id: Int,
    val nombre: String,
    val fecha_creacion: String,
    val personas: List<String>,
    val gastos: List<GastoGrupoResponse>
)

data class GastoGrupoResponse(
    val id: Int,
    val persona: String,
    val monto: Double,
    val descripcion: String,
    val tipo: String,
    val fecha: String
)