package com.example.gestorgastos.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invitaciones")
data class InvitacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val grupoId: Int,
    val grupoNombre: String,
    val invitadoPor: String,
    val invitadoTelefono: String,
    val invitadoNombre: String = "",
    val estado: String = "PENDIENTE",
    val fechaInvitacion: Long = System.currentTimeMillis(),
    val fechaRespuesta: Long? = null,
    val mensaje: String = ""
)
