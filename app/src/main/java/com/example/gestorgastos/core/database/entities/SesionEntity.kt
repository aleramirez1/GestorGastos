package com.example.gestorgastos.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sesiones")
data class SesionEntity(
    @PrimaryKey val usuarioId: Int,
    val username: String,
    val token: String,
    val fechaLogin: String,
    val activa: Boolean = true
)
