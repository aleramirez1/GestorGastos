package com.example.gestorgastos.core.di

import com.example.gestorgastos.core.hardware.data.AndroidActivityManager
import com.example.gestorgastos.core.hardware.data.AndroidAlertManager
import com.example.gestorgastos.core.hardware.data.AndroidCameraManager
import com.example.gestorgastos.core.hardware.data.AndroidFlashlightManager
import com.example.gestorgastos.core.hardware.data.AndroidRotationManager
import com.example.gestorgastos.core.hardware.domain.ActivityManager
import com.example.gestorgastos.core.hardware.domain.AlertManager
import com.example.gestorgastos.core.hardware.domain.CameraManager
import com.example.gestorgastos.core.hardware.domain.FlashlightManager
import com.example.gestorgastos.core.hardware.domain.RotationManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HardwareModule {

    @Binds
    @Singleton
    abstract fun bindCameraManager(impl: AndroidCameraManager): CameraManager

    @Binds
    @Singleton
    abstract fun bindAlertManager(impl: AndroidAlertManager): AlertManager

    @Binds
    @Singleton
    abstract fun bindFlashlightManager(impl: AndroidFlashlightManager): FlashlightManager

    @Binds
    @Singleton
    abstract fun bindRotationManager(impl: AndroidRotationManager): RotationManager

    @Binds
    @Singleton
    abstract fun bindActivityManager(impl: AndroidActivityManager): ActivityManager
}
