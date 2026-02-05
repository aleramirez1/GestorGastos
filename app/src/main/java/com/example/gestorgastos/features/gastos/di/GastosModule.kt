package com.example.gestorgastos.features.gastos.di

import com.example.gestorgastos.core.di.AppContainer
import com.example.gestorgastos.features.gastos.domain.usecases.LoginUseCase
import com.example.gestorgastos.features.gastos.domain.usecases.RegistroUseCase
import com.example.gestorgastos.features.gastos.presentation.viewmodels.AuthViewModelFactory
import com.example.gestorgastos.features.gastos.presentation.viewmodels.GastosViewModelFactory

class GastosModule(private val appContainer: AppContainer) {

    fun provideAuthViewModelFactory(): AuthViewModelFactory {
        return AuthViewModelFactory(
            LoginUseCase(appContainer.authRepository),
            RegistroUseCase(appContainer.authRepository)
        )
    }

    fun provideGastosViewModelFactory(): GastosViewModelFactory {
        return GastosViewModelFactory(appContainer.gastosRepository)
    }

    fun isLoggedIn(): Boolean {
        return appContainer.tokenManager.isLoggedIn()
    }

    fun logout() {
        appContainer.tokenManager.clearAll()
    }
}
