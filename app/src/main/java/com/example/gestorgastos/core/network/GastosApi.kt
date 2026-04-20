package com.example.gestorgastos.core.network

import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GastoCreateRequest
import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GastoEditRequest
import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GrupoRequest
import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GrupoResponse
import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GrupoUpdateRequest
import com.example.gestorgastos.features.login.data.datasources.remote.model.LoginRequest
import com.example.gestorgastos.features.login.data.datasources.remote.model.LoginResponse
import com.example.gestorgastos.features.login.data.datasources.remote.model.PerfilUpdateRequest
import com.example.gestorgastos.features.registro.data.datasources.remote.model.RegistroRequest
import com.example.gestorgastos.features.registro.data.datasources.remote.model.RegistroResponse
import retrofit2.http.*

interface GastosApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/registro")
    suspend fun registro(@Body request: RegistroRequest): RegistroResponse

    @PUT("usuarios/{id}")
    suspend fun actualizarPerfil(@Path("id") id: Int, @Body request: PerfilUpdateRequest): LoginResponse

    @POST("grupos")
    suspend fun crearGrupo(@Body grupo: GrupoRequest): GrupoResponse

    @GET("grupos/usuario/{usuarioId}")
    suspend fun obtenerGrupos(@Path("usuarioId") usuarioId: Int): List<GrupoResponse>

    @GET("grupos/{id}")
    suspend fun obtenerGrupo(@Path("id") id: Int): GrupoResponse

    @PUT("grupos/{id}")
    suspend fun actualizarGrupo(@Path("id") id: Int, @Body grupo: GrupoUpdateRequest): GrupoResponse

    @DELETE("grupos/{id}")
    suspend fun eliminarGrupo(@Path("id") id: Int)

    @POST("grupos/{id}/personas/{persona}")
    suspend fun agregarPersona(@Path("id") id: Int, @Path("persona") persona: String): GrupoResponse

    @DELETE("grupos/{id}/personas/{persona}")
    suspend fun eliminarPersona(@Path("id") id: Int, @Path("persona") persona: String): GrupoResponse

    @POST("grupos/{id}/gastos")
    suspend fun agregarGasto(@Path("id") id: Int, @Body gasto: GastoCreateRequest): GrupoResponse

    @DELETE("grupos/{id}/gastos/{gastoId}")
    suspend fun eliminarGasto(@Path("id") id: Int, @Path("gastoId") gastoId: Int): GrupoResponse

    @PUT("grupos/{id}/gastos/{gastoId}")
    suspend fun editarGasto(@Path("id") id: Int, @Path("gastoId") gastoId: Int, @Body gasto: GastoEditRequest): GrupoResponse

    @POST("grupos/{grupoId}/asociar/{usuarioId}")
    suspend fun asociarUsuarioAGrupo(@Path("grupoId") grupoId: Int, @Path("usuarioId") usuarioId: Int): GrupoResponse

    @GET("grupos/usuario/{usuarioId}/todos")
    suspend fun obtenerTodosGrupos(@Path("usuarioId") usuarioId: Int): List<GrupoResponse>
}
