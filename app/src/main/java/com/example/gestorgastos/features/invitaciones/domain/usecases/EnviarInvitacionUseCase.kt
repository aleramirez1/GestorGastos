package com.example.gestorgastos.features.invitaciones.domain.usecases

import com.example.gestorgastos.features.invitaciones.domain.entities.InvitacionGrupo
import com.example.gestorgastos.features.invitaciones.domain.repositories.InvitacionesRepository
import javax.inject.Inject

class EnviarInvitacionUseCase @Inject constructor(
    private val repository: InvitacionesRepository
) {
    suspend operator fun invoke(invitacion: InvitacionGrupo): Result<String> {
        return repository.enviarInvitacion(invitacion)
    }
}
