package com.example.gestorgastos.core.hardware.data

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import com.example.gestorgastos.core.hardware.domain.FlashlightManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidFlashlightManager @Inject constructor(
    @ApplicationContext private val context: Context
) : FlashlightManager {

    private val cameraManager: CameraManager by lazy {
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private var cameraId: String? = null

    init {
        try {
            cameraId = cameraManager.cameraIdList.firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun turnOn() {
        try {
            cameraId?.let {
                cameraManager.setTorchMode(it, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun turnOff() {
        try {
            cameraId?.let {
                cameraManager.setTorchMode(it, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun hasFlashlight(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }
}
