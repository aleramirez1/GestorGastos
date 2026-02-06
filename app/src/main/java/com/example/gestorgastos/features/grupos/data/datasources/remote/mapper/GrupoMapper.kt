package com.example.gestorgastos.features.grupos.data.datasources.remote.mapper

import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GastoGrupoResponse
import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GrupoResponse
import com.example.gestorgastos.features.grupos.domain.entities.GastoGrupo
import com.example.gestorgastos.features.grupos.domain.entities.Grupo

fun GastoGrupoResponse.toDomain(): GastoGrupo {
    return GastoGrupo(
        id = id,
        persona = persona,
        monto = monto,
        descripcion = descripcion,
        tipo = tipo,
        fecha = fecha
    )
}

fun GrupoResponse.toDomain(): Grupo {
    return Grupo(
        id = id,
        nombre = nombre,
        usuarioId = usuarioId,
        fechaCreacion = fechaCreacion,
        personas = personas,
        gastos = gastos.map { it.toDomain() }
    )
}
