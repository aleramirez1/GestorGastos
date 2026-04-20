package com.example.gestorgastos.features.splash.domain.usecases

import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import javax.inject.Inject

class CheckAuthStatusUseCase @Inject constructor(
    private val tokenManager: TokenManager
) {
    operator fun invoke(): Boolean {
        val token = tokenManager.getToken()
        val userId = tokenManager.getUserId()
        return !token.isNullOrEmpty() && userId != 0
    }
}
