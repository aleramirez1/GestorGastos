package com.example.gestorgastos.features.grupos.di

import com.example.gestorgastos.core.di.AppContainer
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModelFactory

class GruposModule(private val appContainer: AppContainer) {

    fun provideGruposViewModelFactory(): GruposViewModelFactory {
        return GruposViewModelFactory(appContainer.gruposRepository, appContainer.tokenManager)
    }
}
