package com.example.gestorgastos.features.grupos.data.repositories

import com.example.gestorgastos.core.database.dao.GrupoDao
import com.example.gestorgastos.core.database.entities.GrupoEntity
import com.example.gestorgastos.core.network.GastosApi
import com.example.gestorgastos.features.grupos.data.datasources.remote.mapper.toDomain
import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GastoCreateRequest
import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GastoEditRequest
import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GrupoRequest
import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GrupoUpdateRequest
import com.example.gestorgastos.features.grupos.domain.entities.Grupo
import com.example.gestorgastos.features.grupos.domain.repositories.GruposRepository
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import javax.inject.Inject

class GruposRepositoryImpl @Inject constructor(
    private val api: GastosApi,
    private val grupoDao: GrupoDao,
    private val tokenManager: TokenManager
) : GruposRepository {

    private fun GrupoEntity.toDomain() = Grupo(
        id = id, nombre = nombre, usuarioId = usuarioId,
        fechaCreacion = fechaCreacion, personas = personas,
        gastos = emptyList(), fotoTicketUri = fotoTicketUri,
        ganadorRuleta = ganadorRuleta
    )

    private fun Grupo.toEntity() = GrupoEntity(
        id = id, nombre = nombre, usuarioId = usuarioId,
        personas = personas, fechaCreacion = fechaCreacion,
        fotoTicketUri = fotoTicketUri, ganadorRuleta = ganadorRuleta
    )

    override suspend fun crearGrupo(nombre: String, personas: List<String>, usuarioId: Int): Grupo {
        return try {
            val grupo = api.crearGrupo(GrupoRequest(nombre, personas, usuarioId)).toDomain()
            grupoDao.insertGrupo(grupo.toEntity())
            grupo
        } catch (e: Exception) {
            val grupo = Grupo(
                id = System.currentTimeMillis().toInt(),
                nombre = nombre, usuarioId = usuarioId,
                fechaCreacion = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                personas = personas, gastos = emptyList()
            )
            grupoDao.insertGrupo(grupo.toEntity())
            grupo
        }
    }

    override suspend fun obtenerGrupos(usuarioId: Int): List<Grupo> {
        return try {
            val remotos = api.obtenerGrupos(usuarioId).map { it.toDomain() }
            val locales = grupoDao.getGruposByUsuarioSync(usuarioId).associateBy { it.id }
            // Preservar ganadorRuleta guardado localmente, ya que la API no lo maneja
            val entidades = remotos.map { grupo ->
                val ganadorLocal = locales[grupo.id]?.ganadorRuleta
                grupo.copy(ganadorRuleta = ganadorLocal ?: grupo.ganadorRuleta).toEntity()
            }
            grupoDao.deleteGruposByUsuario(usuarioId)
            grupoDao.insertGrupos(entidades)
            entidades.map { it.toDomain() }
        } catch (e: Exception) {
            grupoDao.getGruposByUsuarioSync(usuarioId).map { it.toDomain() }
        }
    }

    override suspend fun obtenerGrupo(id: Int): Grupo {
        return try {
            api.obtenerGrupo(id).toDomain()
        } catch (e: Exception) {
            grupoDao.getGruposByUsuarioSync(tokenManager.getUserId())
                .firstOrNull { it.id == id }?.toDomain()
                ?: throw e
        }
    }

    override suspend fun actualizarGrupo(id: Int, nombre: String?, personas: List<String>?): Grupo {
        return try {
            val grupo = api.actualizarGrupo(id, GrupoUpdateRequest(nombre, personas)).toDomain()
            grupoDao.insertGrupo(grupo.toEntity())
            grupo
        } catch (e: Exception) {
            val local = grupoDao.getGruposByUsuarioSync(tokenManager.getUserId()).first { it.id == id }
            val updated = local.copy(
                nombre = nombre ?: local.nombre,
                personas = personas ?: local.personas
            )
            grupoDao.insertGrupo(updated)
            updated.toDomain()
        }
    }

    override suspend fun eliminarGrupo(id: Int) {
        try { api.eliminarGrupo(id) } catch (_: Exception) {}
        grupoDao.deleteGrupo(id)
    }

    override suspend fun agregarPersona(grupoId: Int, persona: String): Grupo {
        return try {
            val grupo = api.agregarPersona(grupoId, persona).toDomain()
            grupoDao.insertGrupo(grupo.toEntity())
            grupo
        } catch (e: Exception) {
            val local = grupoDao.getGruposByUsuarioSync(tokenManager.getUserId()).first { it.id == grupoId }
            val updated = local.copy(personas = local.personas + persona)
            grupoDao.insertGrupo(updated)
            updated.toDomain()
        }
    }

    override suspend fun eliminarPersona(grupoId: Int, persona: String): Grupo {
        return try {
            val grupo = api.eliminarPersona(grupoId, persona).toDomain()
            grupoDao.insertGrupo(grupo.toEntity())
            grupo
        } catch (e: Exception) {
            val local = grupoDao.getGruposByUsuarioSync(tokenManager.getUserId()).first { it.id == grupoId }
            val updated = local.copy(personas = local.personas.filter { it != persona })
            grupoDao.insertGrupo(updated)
            updated.toDomain()
        }
    }

    override suspend fun agregarGasto(grupoId: Int, persona: String, monto: Double, descripcion: String, tipo: String): Grupo {
        return api.agregarGasto(grupoId, GastoCreateRequest(persona, monto, descripcion, tipo)).toDomain()
    }

    override suspend fun eliminarGasto(grupoId: Int, gastoId: Int): Grupo {
        return api.eliminarGasto(grupoId, gastoId).toDomain()
    }

    override suspend fun editarGasto(grupoId: Int, gastoId: Int, nuevoMonto: Double): Grupo {
        return api.editarGasto(grupoId, gastoId, GastoEditRequest(nuevoMonto)).toDomain()
    }

    override suspend fun guardarGrupoLocal(grupo: Grupo) {
        grupoDao.insertGrupo(grupo.toEntity())
    }

    override suspend fun obtenerGruposLocales(usuarioId: Int): List<Grupo> {
        return grupoDao.getGruposByUsuarioSync(usuarioId).map { it.toDomain() }
    }

    override suspend fun actualizarGrupoLocal(grupo: Grupo) {
        grupoDao.insertGrupo(grupo.toEntity())
    }

    override suspend fun actualizarGrupo(grupo: Grupo) {
        try {
            api.actualizarGrupo(grupo.id, GrupoUpdateRequest(grupo.nombre, grupo.personas))
        } catch (_: Exception) {}
        grupoDao.insertGrupo(grupo.toEntity())
    }

    override suspend fun eliminarGrupoLocal(grupoId: Int) {
        grupoDao.deleteGrupo(grupoId)
    }

    override suspend fun asociarUsuarioAGrupo(grupoId: Int, usuarioId: Int): Grupo {
        return api.asociarUsuarioAGrupo(grupoId, usuarioId).toDomain()
    }
}
