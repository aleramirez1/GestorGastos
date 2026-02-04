package com.example.gestorgastos.features.gastos.domain.entities

data class Gasto(
    val id: Int,
    val monto: Double,
    val descripcion: String,
    val quienPago: String,
    val fecha: String
)

data class ResumenGastos(
    val totalGastado: Double,
    val montoPorPersona: Double,
    val numPersonas: Int,
    val personas: List<String>,
    val deudas: List<Deuda>
)

data class Deuda(
    val persona: String,
    val debe: Double,
    val descripcion: String
)
