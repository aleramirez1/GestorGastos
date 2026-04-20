package com.example.gestorgastos.features.invitaciones.data.repositories

import android.util.Log
import com.example.gestorgastos.core.database.dao.InvitacionDao
import com.example.gestorgastos.core.database.entities.InvitacionEntity
import com.example.gestorgastos.core.notifications.domain.NotificationManager
import com.example.gestorgastos.features.invitaciones.domain.entities.EstadoInvitacion
import com.example.gestorgastos.features.invitaciones.domain.entities.InvitacionGrupo
import com.example.gestorgastos.features.invitaciones.domain.repositories.InvitacionesRepository
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InvitacionesRepositoryImpl @Inject constructor(
    private val invitacionDao: InvitacionDao,
    private val notificationManager: NotificationManager,
    private val tokenManager: TokenManager
) : InvitacionesRepository {

    companion object {
        private const val TAG = "InvitacionesRepo"
    }

    override suspend fun enviarInvitacion(invitacion: InvitacionGrupo): Result<String> {
        return try {
            val entity = InvitacionEntity(
                grupoId = invitacion.grupoId.toInt(),
                grupoNombre = invitacion.grupoNombre,
                invitadoPor = invitacion.invitadoPor,
                invitadoTelefono = invitacion.invitadoTelefono,
                invitadoNombre = invitacion.invitadoNombre,
                mensaje = invitacion.mensaje
            )
            
            val id = invitacionDao.insertInvitacion(entity)

            notificationManager.showLocalNotification(
                title = "Invitación a grupo",
                message = "${invitacion.invitadoPor} te invitó a unirte a ${invitacion.grupoNombre}",
                channelId = "grupos"
            )

            Log.d(TAG, "Invitación enviada: $id")
            Result.success(id.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Error al enviar invitación", e)
            Result.failure(e)
        }
    }

    override suspend fun aceptarInvitacion(invitacionId: String, usuarioId: Int): Result<Unit> {
        return try {
            val id = invitacionId.toInt()
            val invitacion = invitacionDao.getInvitacionById(id)
                ?: return Result.failure(Exception("Invitación no encontrada"))
            invitacionDao.updateEstado(id, EstadoInvitacion.ACEPTADA.name, System.currentTimeMillis())
            notificationManager.showLocalNotification(
                title = "Invitación aceptada",
                message = "Te uniste al grupo ${invitacion.grupoNombre}",
                channelId = "grupos"
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun aceptarInvitacionConNombre(invitacionId: String, usuarioId: Int, nombre: String): Result<Unit> {
        return try {
            val id = invitacionId.toInt()
            val invitacion = invitacionDao.getInvitacionById(id)
                ?: return Result.failure(Exception("Invitación no encontrada"))
            invitacionDao.updateNombre(id, nombre)
            invitacionDao.updateEstado(id, EstadoInvitacion.ACEPTADA.name, System.currentTimeMillis())
            notificationManager.showLocalNotification(
                title = "¡Bienvenido!",
                message = "Te uniste al grupo ${invitacion.grupoNombre} como $nombre",
                channelId = "grupos"
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun buscarInvitacionPorCodigo(codigo: String): Result<InvitacionGrupo> {
        return try {
            val entity = invitacionDao.getInvitacionByCodigo(codigo)
                ?: return Result.failure(Exception("Código no encontrado"))
            Result.success(entity.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rechazarInvitacion(invitacionId: String): Result<Unit> {
        return try {
            val id = invitacionId.toInt()
            invitacionDao.updateEstado(id, EstadoInvitacion.RECHAZADA.name, System.currentTimeMillis())

            Log.d(TAG, "Invitación rechazada: $invitacionId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al rechazar invitación", e)
            Result.failure(e)
        }
    }

    override fun obtenerInvitacionesPendientes(telefono: String): Flow<List<InvitacionGrupo>> {
        return invitacionDao.getInvitacionesPendientes(telefono).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun obtenerInvitacionPorId(invitacionId: String): Result<InvitacionGrupo> {
        return try {
            val id = invitacionId.toInt()
            val entity = invitacionDao.getInvitacionById(id)
                ?: return Result.failure(Exception("Invitación no encontrada"))
            Result.success(entity.toDomain())
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener invitación", e)
            Result.failure(e)
        }
    }

    private fun InvitacionEntity.toDomain() = InvitacionGrupo(
        id = id.toString(),
        grupoId = grupoId.toString(),
        grupoNombre = grupoNombre,
        invitadoPor = invitadoPor,
        invitadoTelefono = invitadoTelefono,
        invitadoNombre = invitadoNombre,
        estado = EstadoInvitacion.valueOf(estado),
        fechaInvitacion = fechaInvitacion,
        fechaRespuesta = fechaRespuesta,
        mensaje = mensaje
    )
}
