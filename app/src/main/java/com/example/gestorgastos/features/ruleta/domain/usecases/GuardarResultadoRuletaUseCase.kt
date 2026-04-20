package com.example.gestorgastos.features.ruleta.domain.usecases

import com.example.gestorgastos.features.ruleta.domain.entities.RuletaResult
import com.example.gestorgastos.features.ruleta.domain.repositories.RuletaRepository
import javax.inject.Inject

class GuardarResultadoRuletaUseCase @Inject constructor(
    private val repository: RuletaRepository
) {
    suspend operator fun invoke(grupoId: Int, ganador: String): RuletaResult {
        return repository.guardarResultado(grupoId, ganador)
    }
}
