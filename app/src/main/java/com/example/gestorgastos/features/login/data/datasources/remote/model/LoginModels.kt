package com.example.gestorgastos.features.login.data.datasources.remote.model

data class LoginRequest(
    val nombre: String,
    val password: String
)

data class LoginResponse(
    val id: Int,
    val nombre: String,
    val email: String,
    val token: String
)
