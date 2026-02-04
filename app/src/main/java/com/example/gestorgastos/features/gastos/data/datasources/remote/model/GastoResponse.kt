package com.example.gestorgastos.features.gastos.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class GastoRequest(
    val monto: Double,
    val descripcion: String,
    @SerializedName("quien_pago")
    val quienPago: String,
    val tipo: String
)

data class GastoResponse(
    val id: Int,
    val monto: Double,
    val descripcion: String,
    @SerializedName("quien_pago")
    val quienPago: String,
    val fecha: String
)

data class ResumenResponse(
    @SerializedName("total_gastado")
    val totalGastado: Double,
    @SerializedName("monto_por_persona")
    val montoPorPersona: Double,
    @SerializedName("num_personas")
    val numPersonas: Int,
    val personas: List<String>,
    val deudas: List<DeudaResponse>
)

data class DeudaResponse(
    val persona: String,
    val debe: Double,
    val descripcion: String
)
