package com.example.gestorgastos.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gestorgastos.core.database.entities.UsuarioEntity

@Dao
interface UsuarioDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: UsuarioEntity): Long
    
    @Query("SELECT * FROM usuarios WHERE username = :username LIMIT 1")
    suspend fun getUsuarioByUsername(username: String): UsuarioEntity?
    
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUsuarioByEmail(email: String): UsuarioEntity?
    
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun getUsuarioById(id: Int): UsuarioEntity?
    
    @Query("UPDATE usuarios SET ultimoLogin = :fecha WHERE id = :usuarioId")
    suspend fun actualizarUltimoLogin(usuarioId: Int, fecha: String)
    
    @Query("SELECT * FROM usuarios")
    suspend fun getAllUsuarios(): List<UsuarioEntity>
    
    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun deleteUsuario(id: Int)
}
