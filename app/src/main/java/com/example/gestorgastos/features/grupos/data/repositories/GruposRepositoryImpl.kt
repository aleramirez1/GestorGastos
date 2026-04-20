package com.example.gestorgastos.features.grupos.data.repositories

import com.example.gestorgastos.core.database.dao.GrupoDao
import com.example.gestorgastos.core.database.entities.GrupoEntity
import com.example.gestorgastos.core.network.GastosApi
import com.example.gestorgastos.features.grupos.data.datasources.remote.mapper.toDomain
import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GastoCreateRequest
import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GastoEditRequest
import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GrupoRequest
import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GrupoUpdateRequest
import com.example.gestorgastos.features.grupos.domain.entities.GastoGrupo
import com.example.gestorgastos.features.grupos.domain.entities.Grupo
import com.example.gestorgastos.features.grupos.domain.repositories.GruposRepository
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import javax.inject.Inject

class GruposRepositoryImpl @Inject constructor(
    private val api: GastosApi,
    private val grupoDao: GrupoDao,
    private val tokenManager: TokenManager
) : GruposRepository {

    private fun mapToDomain(entity: GrupoEntity): Grupo {
        return Grupo(
            id = entity.id,
            nombre = entity.nombre,
            usuarioId = entity.usuarioId,
            fechaCreacion = entity.fechaCreacion,
            personas = entity.personas,
            gastos = emptyList(), 
            fotoTicketUri = entity.fotoTicketUri,
            ganadorRuleta = entity.ganadorRuleta,
            isAhorro = entity.isAhorro,
            metaAhorro = entity.metaAhorro,
            fechaLimite = entity.fechaLimite,
            personasQueYaRecibieron = entity.personasQueYaRecibieron
        )
    }

    private fun Grupo.toEntity() = GrupoEntity(
        id = id, nombre = nombre, usuarioId = usuarioId,
        personas = personas, fechaCreacion = fechaCreacion,
        fotoTicketUri = fotoTicketUri, ganadorRuleta = ganadorRuleta,
        isAhorro = isAhorro,
        metaAhorro = metaAhorro,
        fechaLimite = fechaLimite,
        personasQueYaRecibieron = personasQueYaRecibieron
    )

    override suspend fun crearGrupo(
        nombre: String, 
        personas: List<String>, 
        usuarioId: Int,
        isAhorro: Boolean,
        metaAhorro: Double,
        fechaLimite: String?
    ): Grupo {
        return try {
            val response = api.crearGrupo(
                GrupoRequest(
                    nombre = nombre, 
                    personas = personas, 
                    usuarioId = usuarioId,
                    isAhorro = isAhorro,
                    metaAhorro = metaAhorro,
                    fechaLimite = fechaLimite
                )
            )
            val grupo = response.toDomain()
            grupoDao.insertGrupo(grupo.toEntity())
            grupo
        } catch (e: Exception) {
            val grupo = Grupo(
                id = System.currentTimeMillis().toInt(),
                nombre = nombre, 
                usuarioId = usuarioId,
                fechaCreacion = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                personas = personas, 
                gastos = emptyList(),
                isAhorro = isAhorro,
                metaAhorro = metaAhorro,
                fechaLimite = fechaLimite
            )
            grupoDao.insertGrupo(grupo.toEntity())
            grupo
        }
    }

    override suspend fun obtenerGrupos(usuarioId: Int): List<Grupo> {
        return try {
            val response = api.obtenerGrupos(usuarioId)
            val grupos = response.map { it.toDomain() }
            val locales = grupoDao.getGruposByUsuarioSync(usuarioId)
            
            val gruposParaGuardar = grupos.map { remoto ->
                val local = locales.find { it.id == remoto.id }
                if (local != null && local.isAhorro && !remoto.isAhorro) {
                    remoto.copy(
                        isAhorro = true,
                        metaAhorro = local.metaAhorro,
                        fechaLimite = local.fechaLimite
                    )
                } else {
                    remoto
                }
            }

            grupoDao.deleteGruposByUsuario(usuarioId)
            grupoDao.insertGrupos(gruposParaGuardar.map { it.toEntity() })
            gruposParaGuardar
        } catch (e: Exception) {
            grupoDao.getGruposByUsuarioSync(usuarioId).map { mapToDomain(it) }
        }
    }

    override suspend fun obtenerGrupo(id: Int): Grupo {
        val response = api.obtenerGrupo(id)
        return response.toDomain()
    }

    override suspend fun actualizarGrupo(grupo: Grupo): Grupo {
        val response = api.actualizarGrupo(grupo.id, GrupoUpdateRequest(grupo.nombre, grupo.personas, grupo.ganadorRuleta))
        val grupoActualizado = response.toDomain()
        grupoDao.insertGrupo(grupoActualizado.toEntity())
        return grupoActualizado
    }

    override suspend fun eliminarGrupo(id: Int) {
        try { api.eliminarGrupo(id) } catch (_: Exception) {}
        grupoDao.deleteGrupo(id)
    }

    override suspend fun agregarPersona(grupoId: Int, persona: String): Grupo {
        val response = api.agregarPersona(grupoId, persona)
        val grupoRemoto = response.toDomain()
        val local = grupoDao.getGrupoByIdSync(grupoId)
        val grupoFinal = if (local != null && local.isAhorro && !grupoRemoto.isAhorro) {
            grupoRemoto.copy(isAhorro = true, metaAhorro = local.metaAhorro, fechaLimite = local.fechaLimite)
        } else grupoRemoto
        actualizarGrupoLocal(grupoFinal)
        return grupoFinal
    }

    override suspend fun eliminarPersona(grupoId: Int, persona: String): Grupo {
        val response = api.eliminarPersona(grupoId, persona)
        val grupoRemoto = response.toDomain()
        val local = grupoDao.getGrupoByIdSync(grupoId)
        val grupoFinal = if (local != null && local.isAhorro && !grupoRemoto.isAhorro) {
            grupoRemoto.copy(isAhorro = true, metaAhorro = local.metaAhorro, fechaLimite = local.fechaLimite)
        } else grupoRemoto
        actualizarGrupoLocal(grupoFinal)
        return grupoFinal
    }

    override suspend fun agregarGasto(grupoId: Int, persona: String, monto: Double, descripcion: String, tipo: String, comprobanteUri: String?): Grupo {
        val response = api.agregarGasto(grupoId, GastoCreateRequest(persona, monto, descripcion, tipo, comprobanteUri))
        val grupoRemoto = response.toDomain()
        val local = grupoDao.getGrupoByIdSync(grupoId)
        val grupoFinal = if (local != null && local.isAhorro && !grupoRemoto.isAhorro) {
            grupoRemoto.copy(isAhorro = true, metaAhorro = local.metaAhorro, fechaLimite = local.fechaLimite)
        } else grupoRemoto
        actualizarGrupoLocal(grupoFinal)
        return grupoFinal
    }

    override suspend fun eliminarGasto(grupoId: Int, gastoId: Int): Grupo {
        val response = api.eliminarGasto(grupoId, gastoId)
        val grupoRemoto = response.toDomain()
        val local = grupoDao.getGrupoByIdSync(grupoId)
        val grupoFinal = if (local != null && local.isAhorro && !grupoRemoto.isAhorro) {
            grupoRemoto.copy(isAhorro = true, metaAhorro = local.metaAhorro, fechaLimite = local.fechaLimite)
        } else grupoRemoto
        actualizarGrupoLocal(grupoFinal)
        return grupoFinal
    }

    override suspend fun editarGasto(grupoId: Int, gastoId: Int, nuevoMonto: Double): Grupo {
        val response = api.editarGasto(grupoId, gastoId, GastoEditRequest(nuevoMonto))
        val grupoRemoto = response.toDomain()
        val local = grupoDao.getGrupoByIdSync(grupoId)
        val grupoFinal = if (local != null && local.isAhorro && !grupoRemoto.isAhorro) {
            grupoRemoto.copy(isAhorro = true, metaAhorro = local.metaAhorro, fechaLimite = local.fechaLimite)
        } else grupoRemoto
        actualizarGrupoLocal(grupoFinal)
        return grupoFinal
    }

    override suspend fun guardarGrupoLocal(grupo: Grupo) {
        grupoDao.insertGrupo(grupo.toEntity())
    }

    override suspend fun obtenerGruposLocales(usuarioId: Int): List<Grupo> {
        return grupoDao.getGruposByUsuarioSync(usuarioId).map { mapToDomain(it) }
    }

    override suspend fun actualizarGrupoLocal(grupo: Grupo) {
        grupoDao.insertGrupo(grupo.toEntity())
    }

    override suspend fun eliminarGrupoLocal(grupoId: Int) {
        grupoDao.deleteGrupo(grupoId)
    }
}
