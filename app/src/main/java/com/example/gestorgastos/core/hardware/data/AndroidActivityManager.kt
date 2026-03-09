package com.example.gestorgastos.core.hardware.data

import com.example.gestorgastos.MainActivity
import com.example.gestorgastos.core.hardware.domain.ActivityManager
import javax.inject.Inject

class AndroidActivityManager @Inject constructor() : ActivityManager {

    override fun enableRotation() {
        MainActivity.getInstance()?.enableRotation()
    }

    override fun disableRotation() {
        MainActivity.getInstance()?.disableRotation()
    }
}
