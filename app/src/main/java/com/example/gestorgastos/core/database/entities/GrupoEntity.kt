package com.example.gestorgastos.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grupos")
data class GrupoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val usuarioId: Int,
    val personas: List<String>,
    val fechaCreacion: String = "",
    val fotoTicketUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val ganadorRuleta: String? = null,
    val isAhorro: Boolean = false,
    val metaAhorro: Double = 0.0,
    val fechaLimite: String? = null,
    val personasQueYaRecibieron: List<String> = emptyList()
)
