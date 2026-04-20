package com.example.gestorgastos.core.hardware.data

import android.content.Context
import com.example.gestorgastos.MainActivity
import com.example.gestorgastos.core.hardware.domain.ActivityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidActivityManager @Inject constructor(
    @ApplicationContext private val context: Context
) : ActivityManager {

    override fun enableRotation() {
        MainActivity.getInstance()?.enableRotation()
    }

    override fun disableRotation() {
        MainActivity.getInstance()?.disableRotation()
    }
    
    override fun startActivity(intent: android.content.Intent) {
        context.startActivity(intent)
    }
}
