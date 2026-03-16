package com.example.gestorgastos.features.grupos.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class GrupoRequest(
    val nombre: String,
    val personas: List<String>,
    val usuario_id: Int
)

data class GrupoUpdateRequest(
    val nombre: String? = null,
    val personas: List<String>? = null
)

data class GastoCreateRequest(
    val persona: String,
    val monto: Double,
    val descripcion: String = "",
    val tipo: String = "te_deben"
)

data class GastoEditRequest(
    val monto: Double
)

data class GastoGrupoResponse(
    val id: Int,
    val persona: String,
    val monto: Double,
    val descripcion: String,
    val tipo: String,
    val fecha: String
)

data class GrupoResponse(
    val id: Int,
    val nombre: String,
    @SerializedName("usuario_id")
    val usuarioId: Int,
    @SerializedName("fecha_creacion")
    val fechaCreacion: String,
    val personas: List<String>,
    val gastos: List<GastoGrupoResponse>,
    @SerializedName("foto_ticket_uri")
    val fotoTicketUri: String? = null,
    @SerializedName("ganador_ruleta")
    val ganadorRuleta: String? = null
)
