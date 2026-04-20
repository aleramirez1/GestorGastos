package com.example.gestorgastos.features.grupos.domain.repositories

import com.example.gestorgastos.features.grupos.domain.entities.Grupo

interface GruposRepository {
    suspend fun crearGrupo(
        nombre: String, 
        personas: List<String>, 
        usuarioId: Int,
        isAhorro: Boolean = false,
        metaAhorro: Double = 0.0,
        fechaLimite: String? = null
    ): Grupo

    suspend fun obtenerGrupos(usuarioId: Int): List<Grupo>
    suspend fun obtenerGrupo(id: Int): Grupo
    suspend fun actualizarGrupo(id: Int, nombre: String?, personas: List<String>?): Grupo
    suspend fun eliminarGrupo(id: Int)
    suspend fun agregarPersona(grupoId: Int, persona: String): Grupo
    suspend fun eliminarPersona(grupoId: Int, persona: String): Grupo
    suspend fun agregarGasto(grupoId: Int, persona: String, monto: Double, descripcion: String, tipo: String, comprobanteUri: String? = null): Grupo
    suspend fun eliminarGasto(grupoId: Int, gastoId: Int): Grupo
    suspend fun editarGasto(grupoId: Int, gastoId: Int, nuevoMonto: Double): Grupo
    
    suspend fun guardarGrupoLocal(grupo: Grupo)
    suspend fun obtenerGruposLocales(usuarioId: Int): List<Grupo>
    suspend fun actualizarGrupo(grupo: Grupo)
    suspend fun actualizarGrupoLocal(grupo: Grupo)
    suspend fun eliminarGrupoLocal(grupoId: Int)
}
