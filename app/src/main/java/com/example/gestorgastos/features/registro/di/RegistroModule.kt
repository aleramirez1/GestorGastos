package com.example.gestorgastos.features.registro.di

import com.example.gestorgastos.core.di.AppContainer
import com.example.gestorgastos.features.registro.domain.usecases.RegistroUseCase
import com.example.gestorgastos.features.registro.presentation.viewmodels.RegistroViewModelFactory

class RegistroModule(private val appContainer: AppContainer) {

    private fun provideRegistroUseCase(): RegistroUseCase {
        return RegistroUseCase(appContainer.registroRepository)
    }

    fun provideRegistroViewModelFactory(): RegistroViewModelFactory {
        return RegistroViewModelFactory(provideRegistroUseCase())
    }
}
