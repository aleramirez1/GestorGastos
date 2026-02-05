package com.example.gestorgastos.features.gastos.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class GrupoUpdateRequest(
    @SerializedName("nombre")
    val nombre: String
)