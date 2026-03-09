package com.example.gestorgastos.core.hardware.domain

interface FlashlightManager {
    fun turnOn()
    fun turnOff()
    fun hasFlashlight(): Boolean
}
