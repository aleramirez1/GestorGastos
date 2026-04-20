package com.example.gestorgastos.features.invitaciones.domain.repositories

import com.example.gestorgastos.features.invitaciones.domain.entities.InvitacionGrupo
import kotlinx.coroutines.flow.Flow

interface InvitacionesRepository {
    suspend fun enviarInvitacion(invitacion: InvitacionGrupo): Result<String>
    suspend fun aceptarInvitacion(invitacionId: String, usuarioId: Int): Result<Unit>
    suspend fun aceptarInvitacionConNombre(invitacionId: String, usuarioId: Int, nombre: String): Result<Unit>
    suspend fun rechazarInvitacion(invitacionId: String): Result<Unit>
    fun obtenerInvitacionesPendientes(telefono: String): Flow<List<InvitacionGrupo>>
    suspend fun obtenerInvitacionPorId(invitacionId: String): Result<InvitacionGrupo>
    suspend fun buscarInvitacionPorCodigo(codigo: String): Result<InvitacionGrupo>
}
