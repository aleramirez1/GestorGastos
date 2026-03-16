package com.example.gestorgastos.features.grupos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gestorgastos.core.hardware.domain.AlertManager
import com.example.gestorgastos.core.hardware.domain.CameraManager
import com.example.gestorgastos.core.hardware.domain.FlashlightManager
import com.example.gestorgastos.features.grupos.domain.repositories.GruposRepository
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager

class GruposViewModelFactory(
    private val repository: GruposRepository,
    private val tokenManager: TokenManager,
    private val cameraManager: CameraManager?,
    private val alertManager: AlertManager,
    private val flashlightManager: FlashlightManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val camera = cameraManager ?: object : CameraManager {
            override suspend fun takePicture(): Result<android.net.Uri> {
                return Result.failure(Exception("Cámara no disponible"))
            }
            override fun hasCamera(): Boolean = false
        }
        return GruposViewModel(repository, tokenManager, camera, alertManager, flashlightManager) as T
    }
}
