package com.example.gestorgastos.features.ruleta.domain.repositories

import com.example.gestorgastos.features.ruleta.domain.entities.RuletaResult

interface RuletaRepository {
    suspend fun guardarResultado(grupoId: Int, ganador: String): RuletaResult
    suspend fun obtenerResultados(usuarioId: Int): List<RuletaResult>
}
