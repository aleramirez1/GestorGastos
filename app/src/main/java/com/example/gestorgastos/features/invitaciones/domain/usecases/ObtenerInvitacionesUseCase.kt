package com.example.gestorgastos.features.invitaciones.domain.usecases

import com.example.gestorgastos.features.invitaciones.domain.entities.InvitacionGrupo
import com.example.gestorgastos.features.invitaciones.domain.repositories.InvitacionesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerInvitacionesUseCase @Inject constructor(
    private val repository: InvitacionesRepository
) {
    operator fun invoke(telefono: String): Flow<List<InvitacionGrupo>> {
        return repository.obtenerInvitacionesPendientes(telefono)
    }

    suspend fun buscarPorCodigo(codigo: String): Result<InvitacionGrupo> {
        return repository.buscarInvitacionPorCodigo(codigo)
    }
}
