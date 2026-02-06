package com.example.gestorgastos.features.grupos.domain.entities

data class GastoGrupo(
    val id: Int,
    val persona: String,
    val monto: Double,
    val descripcion: String,
    val tipo: String,
    val fecha: String
)

data class Grupo(
    val id: Int,
    val nombre: String,
    val usuarioId: Int,
    val fechaCreacion: String,
    val personas: List<String>,
    val gastos: List<GastoGrupo>
)
