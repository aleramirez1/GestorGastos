package com.example.gestorgastos.core.hardware.data

import android.content.Context
import android.provider.Settings
import com.example.gestorgastos.core.hardware.domain.RotationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidRotationManager @Inject constructor(
    @ApplicationContext private val context: Context
) : RotationManager {

    override fun enableAutoRotation() {
        try {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.ACCELEROMETER_ROTATION,
                1
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun disableAutoRotation() {
        try {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.ACCELEROMETER_ROTATION,
                0
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun isAutoRotationEnabled(): Boolean {
        return try {
            Settings.System.getInt(
                context.contentResolver,
                Settings.System.ACCELEROMETER_ROTATION,
                0
            ) == 1
        } catch (e: Exception) {
            false
        }
    }
}
