package com.example.gestorgastos.features.gastos.data.repositories

import com.example.gestorgastos.core.network.GastosApi
import com.example.gestorgastos.features.gastos.data.datasources.remote.mapper.toDomain
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.GastoCreateRequest
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.GrupoRequest
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.GrupoUpdateRequest
import com.example.gestorgastos.features.gastos.domain.entities.Grupo
import com.example.gestorgastos.features.gastos.domain.repositories.GastosRepository

class GastosRepositoryImpl(
    private val api: GastosApi
) : GastosRepository {

    override suspend fun crearGrupo(nombre: String, personas: List<String>): Grupo {
        return api.crearGrupo(GrupoRequest(nombre, personas)).toDomain()
    }

    override suspend fun obtenerGrupos(): List<Grupo> {
        return api.obtenerGrupos().map { it.toDomain() }
    }

    override suspend fun obtenerGrupo(id: Int): Grupo {
        return api.obtenerGrupo(id).toDomain()
    }

    override suspend fun actualizarGrupo(id: Int, nombre: String?, personas: List<String>?): Grupo {
        return api.actualizarGrupo(id, GrupoUpdateRequest(nombre, personas)).toDomain()
    }

    override suspend fun eliminarGrupo(id: Int) {
        api.eliminarGrupo(id)
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
}
