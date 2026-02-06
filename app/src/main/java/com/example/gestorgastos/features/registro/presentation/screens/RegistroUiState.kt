package com.example.gestorgastos.features.registro.presentation.screens

data class RegistroUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
