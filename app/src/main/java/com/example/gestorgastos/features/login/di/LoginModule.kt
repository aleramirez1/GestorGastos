package com.example.gestorgastos.features.login.di

import com.example.gestorgastos.core.di.AppContainer
import com.example.gestorgastos.features.login.domain.usecases.LoginUseCase
import com.example.gestorgastos.features.login.presentation.viewmodels.LoginViewModelFactory

class LoginModule(private val appContainer: AppContainer) {

    private fun provideLoginUseCase(): LoginUseCase {
        return LoginUseCase(appContainer.loginRepository)
    }

    fun provideLoginViewModelFactory(): LoginViewModelFactory {
        return LoginViewModelFactory(provideLoginUseCase())
    }
}
