package com.example.gestorgastos.core.notifications.domain

interface NotificationManager {
    fun showLocalNotification(title: String, message: String, channelId: String = "default")
}
