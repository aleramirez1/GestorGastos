package com.example.gestorgastos.core.notifications.data

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dagger.hilt.EntryPoint

@EntryPoint
@dagger.hilt.InstallIn(SingletonComponent::class)
interface FCMServiceEntryPoint {
    fun notificationManager(): com.example.gestorgastos.core.notifications.domain.NotificationManager
}

class FCMService : FirebaseMessagingService() {

    private val notificationManager by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            FCMServiceEntryPoint::class.java
        ).notificationManager()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMService", "Nuevo token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCMService", "Mensaje recibido de: ${message.from}")

        message.notification?.let {
            notificationManager.showLocalNotification(
                title = it.title ?: "Gestor de Gastos",
                message = it.body ?: "",
                channelId = message.data["channel_id"] ?: "default"
            )
        } ?: run {
            if (message.data.isNotEmpty()) {
                notificationManager.showLocalNotification(
                    title = message.data["title"] ?: "Gestor de Gastos",
                    message = message.data["body"] ?: "",
                    channelId = message.data["channel_id"] ?: "default"
                )
            }
        }
    }
}
