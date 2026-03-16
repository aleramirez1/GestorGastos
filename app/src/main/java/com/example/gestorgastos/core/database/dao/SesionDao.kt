package com.example.gestorgastos.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gestorgastos.core.database.entities.SesionEntity

@Dao
interface SesionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSesion(sesion: SesionEntity)
    
    @Query("SELECT * FROM sesiones WHERE activa = 1 LIMIT 1")
    suspend fun getSesionActiva(): SesionEntity?
    
    @Query("SELECT * FROM sesiones WHERE usuarioId = :usuarioId LIMIT 1")
    suspend fun getSesionByUsuarioId(usuarioId: Int): SesionEntity?
    
    @Query("UPDATE sesiones SET activa = 0 WHERE usuarioId = :usuarioId")
    suspend fun cerrarSesion(usuarioId: Int)
    
    @Query("UPDATE sesiones SET activa = 0")
    suspend fun cerrarTodasLasSesiones()
    
    @Query("DELETE FROM sesiones WHERE usuarioId = :usuarioId")
    suspend fun deleteSesion(usuarioId: Int)
}
