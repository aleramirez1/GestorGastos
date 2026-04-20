package com.example.gestorgastos.core.notifications.di

import com.example.gestorgastos.core.notifications.data.LocalNotificationManager
import com.example.gestorgastos.core.notifications.domain.NotificationManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationManager(
        localNotificationManager: LocalNotificationManager
    ): NotificationManager
}
