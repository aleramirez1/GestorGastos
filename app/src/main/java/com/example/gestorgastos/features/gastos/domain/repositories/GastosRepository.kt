package com.example.gestorgastos.features.gastos.domain.repositories

import com.example.gestorgastos.features.gastos.domain.entities.Grupo

interface GastosRepository {
    suspend fun crearGrupo(nombre: String, personas: List<String>, usuarioId: Int): Grupo
    suspend fun obtenerGrupos(usuarioId: Int): List<Grupo>
    suspend fun obtenerGrupo(id: Int): Grupo
    suspend fun actualizarGrupo(id: Int, nombre: String?, personas: List<String>?): Grupo
    suspend fun eliminarGrupo(id: Int)
    suspend fun agregarPersona(grupoId: Int, persona: String): Grupo
    suspend fun eliminarPersona(grupoId: Int, persona: String): Grupo
    suspend fun agregarGasto(grupoId: Int, persona: String, monto: Double, descripcion: String, tipo: String): Grupo
    suspend fun eliminarGasto(grupoId: Int, gastoId: Int): Grupo
    suspend fun editarGasto(grupoId: Int, gastoId: Int, nuevoMonto: Double): Grupo
}
