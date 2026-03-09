package com.example.gestorgastos.core.hardware.data

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.gestorgastos.core.hardware.domain.AlertManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import javax.inject.Inject

class AndroidAlertManager @Inject constructor(
    @ApplicationContext private val context: Context
) : AlertManager {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val cameraId: String? by lazy {
        try {
            cameraManager.cameraIdList.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun vibrate(durationMillis: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(durationMillis, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMillis)
        }
    }

    override suspend fun flashBlink(durationMillis: Long) {
        if (!hasFlash()) return
        turnFlashOn()
        delay(durationMillis)
        turnFlashOff()
    }

    override fun turnFlashOn() {
        setFlashState(true)
    }

    override fun turnFlashOff() {
        setFlashState(false)
    }

    override fun hasFlash(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    private fun setFlashState(isEnabled: Boolean) {
        cameraId?.let {
            try {
                cameraManager.setTorchMode(it, isEnabled)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
