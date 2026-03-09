package com.example.gestorgastos.core.hardware.domain

import android.net.Uri

interface CameraManager {
    suspend fun takePicture(): Result<Uri>
    fun hasCamera(): Boolean
}
