package com.example.gestorgastos.core.database.dao

import androidx.room.*
import com.example.gestorgastos.core.database.entities.GastoEntity
import kotlinx.coroutines.flow.Flow

// DAO para Gastos
@Dao
interface GastoDao {
    @Query("SELECT * FROM gastos WHERE grupoId = :grupoId ORDER BY fecha DESC")
    fun getGastosByGrupo(grupoId: Int): Flow<List<GastoEntity>>

    @Query("SELECT * FROM gastos WHERE isSynced = 0")
    suspend fun getUnsyncedGastos(): List<GastoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGasto(gasto: GastoEntity): Long

    @Update
    suspend fun updateGasto(gasto: GastoEntity)

    @Query("DELETE FROM gastos WHERE localId = :localId")
    suspend fun deleteGastoLocally(localId: Int)
    
    @Query("DELETE FROM gastos WHERE grupoId = :grupoId")
    suspend fun deleteGastosByGrupo(grupoId: Int)
}
