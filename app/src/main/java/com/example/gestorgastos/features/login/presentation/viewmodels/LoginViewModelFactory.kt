package com.example.gestorgastos.features.login.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gestorgastos.core.hardware.domain.ActivityManager
import com.example.gestorgastos.core.hardware.domain.RotationManager
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import com.example.gestorgastos.features.login.domain.usecases.LoginUseCase

class LoginViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager,
    private val rotationManager: RotationManager,
    private val activityManager: ActivityManager,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(loginUseCase, tokenManager, rotationManager, activityManager, context) as T
    }
}
