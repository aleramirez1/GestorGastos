package com.example.gestorgastos.features.grupos.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class GrupoRequest(
    val nombre: String,
    val personas: List<String>,
    @SerializedName("usuario_id")
    val usuarioId: Int,
    @SerializedName("is_ahorro")
    val isAhorro: Boolean = false,
    @SerializedName("meta_ahorro")
    val metaAhorro: Double = 0.0,
    @SerializedName("fecha_limite")
    val fechaLimite: String? = null
)

data class GrupoUpdateRequest(
    val nombre: String? = null,
    val personas: List<String>? = null,
    @SerializedName("ganador_ruleta")
    val ganadorRuleta: String? = null,
    @SerializedName("personas_ya_recibieron")
    val personasYaRecibieron: List<String>? = null
)

data class GastoCreateRequest(
    val persona: String,
    val monto: Double,
    val descripcion: String = "",
    val tipo: String = "te_deben",
    @SerializedName("comprobante_uri")
    val comprobanteUri: String? = null
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
    val fecha: String,
    @SerializedName("comprobante_uri")
    val comprobanteUri: String? = null
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
    val ganadorRuleta: String? = null,
    @SerializedName("is_ahorro")
    val isAhorro: Boolean = false,
    @SerializedName("meta_ahorro")
    val metaAhorro: Double = 0.0,
    @SerializedName("fecha_limite")
    val fechaLimite: String? = null,
    @SerializedName("personas_ya_recibieron")
    val personasYaRecibieron: List<String> = emptyList()
)
