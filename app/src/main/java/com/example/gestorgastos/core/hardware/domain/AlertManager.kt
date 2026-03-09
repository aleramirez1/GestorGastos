package com.example.gestorgastos.core.hardware.domain

interface AlertManager {
    fun vibrate(durationMillis: Long = 500)
    suspend fun flashBlink(durationMillis: Long = 100)
    fun turnFlashOn()
    fun turnFlashOff()
    fun hasFlash(): Boolean
}
