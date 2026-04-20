package com.example.gestorgastos.features.invitaciones.domain.usecases

import com.example.gestorgastos.features.invitaciones.domain.repositories.InvitacionesRepository
import javax.inject.Inject

class ResponderInvitacionUseCase @Inject constructor(
    private val repository: InvitacionesRepository
) {
    suspend fun aceptar(invitacionId: String, usuarioId: Int): Result<Unit> {
        return repository.aceptarInvitacion(invitacionId, usuarioId)
    }

    suspend fun aceptarConNombre(invitacionId: String, usuarioId: Int, nombre: String): Result<Unit> {
        return repository.aceptarInvitacionConNombre(invitacionId, usuarioId, nombre)
    }

    suspend fun rechazar(invitacionId: String): Result<Unit> {
        return repository.rechazarInvitacion(invitacionId)
    }
}
