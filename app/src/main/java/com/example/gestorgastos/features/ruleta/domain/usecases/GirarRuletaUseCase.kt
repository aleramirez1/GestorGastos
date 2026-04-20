package com.example.gestorgastos.features.ruleta.domain.usecases

import javax.inject.Inject
import kotlin.random.Random

class GirarRuletaUseCase @Inject constructor() {
    
    operator fun invoke(participantes: List<String>): String {
        require(participantes.isNotEmpty()) { "La lista de participantes no puede estar vacía" }
        val ganadorIndex = Random.nextInt(participantes.size)
        return participantes[ganadorIndex]
    }
}
