package com.example.gestorgastos.features.login.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val nombre: String,
    val password: String
)

data class LoginResponse(
    val id: Int,
    val nombre: String,
    val email: String,
    val token: String,
    @SerializedName("foto_perfil")
    val fotoPerfil: String? = null
)

data class PerfilUpdateRequest(
    val nombre: String,
    @SerializedName("foto_perfil")
    val fotoPerfil: String? = null
)
