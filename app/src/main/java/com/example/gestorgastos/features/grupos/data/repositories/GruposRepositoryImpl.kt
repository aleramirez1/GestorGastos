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

    override suspend fun crearGrupo(nombre: String, personas: List<String>, usuarioId: Int): Grupo {
        val grupo = api.crearGrupo(GrupoRequest(nombre, personas, usuarioId)).toDomain()
        grupoDao.insertGrupos(listOf(GrupoEntity(grupo.id, grupo.nombre, grupo.usuarioId, grupo.personas)))
        return grupo
    }

    override suspend fun obtenerGrupos(usuarioId: Int): List<Grupo> {
        return try {
            val grupos = api.obtenerGrupos(usuarioId).map { it.toDomain() }
            grupoDao.deleteGruposByUsuario(usuarioId)
            grupoDao.insertGrupos(grupos.map { GrupoEntity(it.id, it.nombre, it.usuarioId, it.personas) })
            grupos
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun obtenerGrupo(id: Int): Grupo {
        return api.obtenerGrupo(id).toDomain()
    }

    override suspend fun actualizarGrupo(id: Int, nombre: String?, personas: List<String>?): Grupo {
        return api.actualizarGrupo(id, GrupoUpdateRequest(nombre, personas)).toDomain()
    }

    override suspend fun eliminarGrupo(id: Int) {
        api.eliminarGrupo(id)
        grupoDao.deleteGrupo(id)
    }

    override suspend fun agregarPersona(grupoId: Int, persona: String): Grupo {
        return api.agregarPersona(grupoId, persona).toDomain()
    }

    override suspend fun eliminarPersona(grupoId: Int, persona: String): Grupo {
        return api.eliminarPersona(grupoId, persona).toDomain()
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
        val entity = GrupoEntity(
            id = grupo.id,
            nombre = grupo.nombre,
            usuarioId = grupo.usuarioId,
            personas = grupo.personas,
            fechaCreacion = grupo.fechaCreacion,
            fotoTicketUri = grupo.fotoTicketUri,
            ganadorRuleta = grupo.ganadorRuleta
        )
        grupoDao.insertGrupo(entity)
    }
    
    override suspend fun obtenerGruposLocales(usuarioId: Int): List<Grupo> {
        return grupoDao.getGruposByUsuarioSync(usuarioId).map { entity ->
            Grupo(
                id = entity.id,
                nombre = entity.nombre,
                usuarioId = entity.usuarioId,
                fechaCreacion = entity.fechaCreacion,
                personas = entity.personas,
                gastos = emptyList(),
                fotoTicketUri = entity.fotoTicketUri,
                ganadorRuleta = entity.ganadorRuleta
            )
        }
    }
    
    override suspend fun actualizarGrupoLocal(grupo: Grupo) {
        guardarGrupoLocal(grupo)
    }
}
