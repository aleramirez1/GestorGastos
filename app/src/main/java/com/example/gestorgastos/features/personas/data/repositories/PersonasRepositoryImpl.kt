package com.example.gestorgastos.features.personas.data.repositories

import com.example.gestorgastos.features.grupos.domain.repositories.GruposRepository
import com.example.gestorgastos.features.personas.domain.entities.PersonaGanadora
import com.example.gestorgastos.features.personas.domain.repositories.PersonasRepository
import javax.inject.Inject

class PersonasRepositoryImpl @Inject constructor(
    private val gruposRepository: GruposRepository
) : PersonasRepository {

    override suspend fun obtenerPersonasGanadoras(usuarioId: Int): List<PersonaGanadora> {
        val grupos = gruposRepository.obtenerGrupos(usuarioId)
        return grupos.mapNotNull { grupo ->
            grupo.ganadorRuleta?.let { ganador ->
                PersonaGanadora(
                    nombre = ganador,
                    nombreGrupo = grupo.nombre,
                    fecha = grupo.fechaCreacion,
                    grupoId = grupo.id
                )
            }
        }
    }
}
