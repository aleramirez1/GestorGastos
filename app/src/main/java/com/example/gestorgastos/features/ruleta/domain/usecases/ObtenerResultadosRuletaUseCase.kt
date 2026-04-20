package com.example.gestorgastos.features.ruleta.domain.usecases

import com.example.gestorgastos.features.ruleta.domain.entities.RuletaResult
import com.example.gestorgastos.features.ruleta.domain.repositories.RuletaRepository
import javax.inject.Inject

class ObtenerResultadosRuletaUseCase @Inject constructor(
    private val repository: RuletaRepository
) {
    suspend operator fun invoke(usuarioId: Int): List<RuletaResult> {
        return repository.obtenerResultados(usuarioId)
    }
}
