package com.example.gestorgastos.features.gastos.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class GrupoRequest(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("personas")
    val personas: List<String>
)