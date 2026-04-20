package com.example.gestorgastos.features.personas.domain.repositories

import com.example.gestorgastos.features.personas.domain.entities.PersonaGanadora

interface PersonasRepository {
    suspend fun obtenerPersonasGanadoras(usuarioId: Int): List<PersonaGanadora>
}
