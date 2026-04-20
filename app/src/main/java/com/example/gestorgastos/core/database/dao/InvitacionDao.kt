package com.example.gestorgastos.core.database.dao

import androidx.room.*
import com.example.gestorgastos.core.database.entities.InvitacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvitacionDao {
    @Insert
    suspend fun insertInvitacion(invitacion: InvitacionEntity): Long

    @Update
    suspend fun updateInvitacion(invitacion: InvitacionEntity)

    @Query("SELECT * FROM invitaciones WHERE invitadoTelefono = :telefono AND estado = 'PENDIENTE' ORDER BY fechaInvitacion DESC")
    fun getInvitacionesPendientes(telefono: String): Flow<List<InvitacionEntity>>

    @Query("SELECT * FROM invitaciones WHERE id = :id")
    suspend fun getInvitacionById(id: Int): InvitacionEntity?

    @Query("UPDATE invitaciones SET estado = :estado, fechaRespuesta = :fechaRespuesta WHERE id = :id")
    suspend fun updateEstado(id: Int, estado: String, fechaRespuesta: Long)

    @Query("SELECT * FROM invitaciones WHERE mensaje = :codigo LIMIT 1")
    suspend fun getInvitacionByCodigo(codigo: String): InvitacionEntity?

    @Query("UPDATE invitaciones SET invitadoNombre = :nombre WHERE id = :id")
    suspend fun updateNombre(id: Int, nombre: String)
}
