package com.example.gestorgastos.core.hardware.domain

interface RotationManager {
    fun enableAutoRotation()
    fun disableAutoRotation()
    fun isAutoRotationEnabled(): Boolean
}
