package com.example.gestorgastos.features.personas.domain.usecases

import com.example.gestorgastos.features.personas.domain.entities.PersonaGanadora
import com.example.gestorgastos.features.personas.domain.repositories.PersonasRepository
import javax.inject.Inject

class ObtenerPersonasGanadorasUseCase @Inject constructor(
    private val repository: PersonasRepository
) {
    suspend operator fun invoke(usuarioId: Int): List<PersonaGanadora> {
        return repository.obtenerPersonasGanadoras(usuarioId)
    }
}
