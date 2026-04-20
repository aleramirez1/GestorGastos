package com.example.gestorgastos.core.hardware.domain

interface ActivityManager {
    fun enableRotation()
    fun disableRotation()
    fun startActivity(intent: android.content.Intent)
}
