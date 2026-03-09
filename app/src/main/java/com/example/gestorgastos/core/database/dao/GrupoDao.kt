package com.example.gestorgastos.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gestorgastos.core.database.entities.GrupoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrupoDao {
    
    @Query("SELECT * FROM grupos WHERE usuarioId = :usuarioId ORDER BY timestamp DESC")
    fun getGruposByUsuario(usuarioId: Int): Flow<List<GrupoEntity>>
    
    @Query("SELECT * FROM grupos WHERE usuarioId = :usuarioId ORDER BY timestamp DESC")
    suspend fun getGruposByUsuarioSync(usuarioId: Int): List<GrupoEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrupos(grupos: List<GrupoEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrupo(grupo: GrupoEntity)
    
    @Query("DELETE FROM grupos WHERE usuarioId = :usuarioId")
    suspend fun deleteGruposByUsuario(usuarioId: Int)
    
    @Query("DELETE FROM grupos WHERE id = :grupoId")
    suspend fun deleteGrupo(grupoId: Int)
}
