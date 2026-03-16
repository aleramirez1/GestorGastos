package com.example.gestorgastos.features.registro.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gestorgastos.core.database.dao.UsuarioDao
import com.example.gestorgastos.features.registro.domain.usecases.RegistroUseCase

class RegistroViewModelFactory(
    private val registroUseCase: RegistroUseCase,
    private val usuarioDao: UsuarioDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegistroViewModel(registroUseCase, usuarioDao) as T
    }
}
