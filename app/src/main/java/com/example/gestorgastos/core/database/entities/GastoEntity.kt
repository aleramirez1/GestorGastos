package com.example.gestorgastos.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos")
data class GastoEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val serverId: Int = 0,
    val grupoId: Int,
    val persona: String,
    val monto: Double,
    val descripcion: String,
    val tipo: String, // "te_deben", "tu_debes", "abono"
    val fecha: String,
    val comprobanteUri: String? = null,
    val isSynced: Boolean = false
)
