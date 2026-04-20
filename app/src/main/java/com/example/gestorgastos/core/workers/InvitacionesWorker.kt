package com.example.gestorgastos.core.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gestorgastos.core.notifications.domain.NotificationManager
import com.example.gestorgastos.features.invitaciones.domain.repositories.InvitacionesRepository
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class InvitacionesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val invitacionesRepository: InvitacionesRepository,
    private val notificationManager: NotificationManager,
    private val tokenManager: TokenManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val telefono = tokenManager.getUserName() ?: ""
            if (telefono.isEmpty()) {
                return Result.success()
            }

            val invitaciones = invitacionesRepository.obtenerInvitacionesPendientes(telefono).first()
            
            if (invitaciones.isNotEmpty()) {
                val count = invitaciones.size
                notificationManager.showLocalNotification(
                    title = "Invitaciones pendientes",
                    message = "Tienes $count invitación${if (count > 1) "es" else ""} pendiente${if (count > 1) "s" else ""}",
                    channelId = "grupos"
                )
                Log.d(TAG, "Notificación de invitaciones enviada: $count")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error en InvitacionesWorker", e)
            Result.retry()
        }
    }

    companion object {
        const val TAG = "InvitacionesWorker"
        const val WORK_NAME = "invitaciones_check"
    }
}
