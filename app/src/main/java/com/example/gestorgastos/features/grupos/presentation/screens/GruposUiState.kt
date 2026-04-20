package com.example.gestorgastos.features.grupos.presentation.screens

import com.example.gestorgastos.features.grupos.domain.entities.Grupo

data class GruposUiState(
    val isLoading: Boolean = false,
    val grupos: List<Grupo> = emptyList(),
    val searchQuery: String = "",
    val grupoActual: Grupo? = null,
    val error: String? = null,
    val fotoGastoUri: String? = null,
    val ganadorRuleta: String? = null,
    val fotoPerfil: String? = null,
    val nombreUsuario: String? = null
) {
    val gruposFiltrados: List<Grupo>
        get() = if (searchQuery.isBlank()) {
            grupos
        } else {
            grupos.filter { it.nombre.contains(searchQuery, ignoreCase = true) }
        }
}
