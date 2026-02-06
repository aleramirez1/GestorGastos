package com.example.gestorgastos.features.login.presentation.screens

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
