package com.example.gestorgastos.features.gastos.data.datasources.remote.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegistroRequest(
    val nombre: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val id: Int,
    val nombre: String,
    val email: String,
    val token: String
)


