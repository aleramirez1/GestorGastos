package com.example.gestorgastos.features.gastos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gestorgastos.features.gastos.domain.usecases.LoginUseCase
import com.example.gestorgastos.features.gastos.domain.usecases.RegistroUseCase

class AuthViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val registroUseCase: RegistroUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(loginUseCase, registroUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
