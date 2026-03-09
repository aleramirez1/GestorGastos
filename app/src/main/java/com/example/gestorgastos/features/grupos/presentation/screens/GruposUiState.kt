package com.example.gestorgastos.features.grupos.presentation.screens

import com.example.gestorgastos.features.grupos.domain.entities.Grupo

data class GruposUiState(
    val isLoading: Boolean = false,
    val grupos: List<Grupo> = emptyList(),
    val grupoActual: Grupo? = null,
    val error: String? = null,
    val fotoGastoUri: String? = null,
    val ganadorRuleta: String? = null
)
