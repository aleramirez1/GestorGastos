package com.example.gestorgastos.features.ruleta.data.repositories

import com.example.gestorgastos.features.grupos.domain.repositories.GruposRepository
import com.example.gestorgastos.features.ruleta.domain.entities.RuletaResult
import com.example.gestorgastos.features.ruleta.domain.repositories.RuletaRepository
import javax.inject.Inject

class RuletaRepositoryImpl @Inject constructor(
    private val gruposRepository: GruposRepository
) : RuletaRepository {

    override suspend fun guardarResultado(grupoId: Int, ganador: String): RuletaResult {
        val grupo = gruposRepository.obtenerGrupo(grupoId)
        val grupoActualizado = grupo.copy(ganadorRuleta = ganador)
        gruposRepository.actualizarGrupo(grupoActualizado)
        return RuletaResult(ganador = ganador, participantes = grupo.personas)
    }

    override suspend fun obtenerResultados(usuarioId: Int): List<RuletaResult> {
        val grupos = gruposRepository.obtenerGrupos(usuarioId)
        return grupos.mapNotNull { grupo ->
            grupo.ganadorRuleta?.let { ganador ->
                RuletaResult(ganador = ganador, participantes = grupo.personas)
            }
        }
    }
}
