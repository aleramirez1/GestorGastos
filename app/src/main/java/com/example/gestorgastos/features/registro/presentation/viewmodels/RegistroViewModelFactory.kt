package com.example.gestorgastos.features.registro.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gestorgastos.features.registro.domain.usecases.RegistroUseCase

class RegistroViewModelFactory(private val registroUseCase: RegistroUseCase) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegistroViewModel(registroUseCase) as T
    }
}
