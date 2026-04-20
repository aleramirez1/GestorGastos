package com.example.gestorgastos.core.notifications.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.gestorgastos.R
import com.example.gestorgastos.core.notifications.domain.NotificationManager as DomainNotificationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) : DomainNotificationManager {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val channels = listOf(
            NotificationChannel(
                "default",
                "Notificaciones Generales",
                NotificationManager.IMPORTANCE_DEFAULT
            ),
            NotificationChannel(
                "grupos",
                "Grupos",
                NotificationManager.IMPORTANCE_HIGH
            ),
            NotificationChannel(
                "ruleta",
                "Ruleta",
                NotificationManager.IMPORTANCE_HIGH
            )
        )
        
        channels.forEach { channel ->
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun showLocalNotification(title: String, message: String, channelId: String) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
