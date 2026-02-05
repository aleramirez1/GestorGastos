package com.example.gestorgastos.core.network

import com.example.gestorgastos.features.gastos.data.datasources.remote.model.AuthResponse
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.GastoCreateRequest
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.GrupoRequest
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.GrupoResponse
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.GrupoUpdateRequest
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.LoginRequest
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.RegistroRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GastosApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/registro")
    suspend fun registro(@Body request: RegistroRequest): AuthResponse

    @POST("grupos")
    suspend fun crearGrupo(@Body grupo: GrupoRequest): GrupoResponse

    @GET("grupos")
    suspend fun obtenerGrupos(): List<GrupoResponse>

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
}
