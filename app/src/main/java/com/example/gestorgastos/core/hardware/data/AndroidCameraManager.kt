package com.example.gestorgastos.core.hardware.data

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.gestorgastos.core.hardware.domain.CameraManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class AndroidCameraManager @Inject constructor(
    @ApplicationContext private val context: Context
) : CameraManager {

    override suspend fun takePicture(): Result<Uri> {
        return try {
            val photoFile = createImageFile()
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            Result.success(uri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun hasCamera(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    private fun createImageFile(): File {
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile(
            "gasto_${System.currentTimeMillis()}",
            ".jpg",
            storageDir
        )
    }
}
