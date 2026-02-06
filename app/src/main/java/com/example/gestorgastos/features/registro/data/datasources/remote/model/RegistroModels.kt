package com.example.gestorgastos.features.registro.data.datasources.remote.model

data class RegistroRequest(
    val nombre: String,
    val email: String,
    val password: String
)

data class RegistroResponse(
    val id: Int,
    val nombre: String,
    val email: String,
    val token: String
)
