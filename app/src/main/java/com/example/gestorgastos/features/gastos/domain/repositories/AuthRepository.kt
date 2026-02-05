package com.example.gestorgastos.features.gastos.domain.repositories

import com.example.gestorgastos.features.gastos.domain.entities.Usuario

interface AuthRepository {
    suspend fun login(nombre: String, password: String): Usuario
    suspend fun registro(nombre: String, email: String, password: String): Usuario
    fun logout()
    fun isLoggedIn(): Boolean
}
