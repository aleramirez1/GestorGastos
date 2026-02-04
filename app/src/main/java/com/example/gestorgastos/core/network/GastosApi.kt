package com.example.gestorgastos.core.network

import com.example.gestorgastos.features.gastos.data.datasources.remote.model.AuthResponse
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.GastoRequest
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.GastoResponse
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.LoginRequest
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.RegistroRequest
import com.example.gestorgastos.features.gastos.data.datasources.remote.model.ResumenResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GastosApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/registro")
    suspend fun registro(@Body request: RegistroRequest): AuthResponse

    @POST("gastos")
    suspend fun crearGasto(@Body gasto: GastoRequest): GastoResponse

    @GET("gastos")
    suspend fun obtenerGastos(): List<GastoResponse>

    @GET("resumen")
    suspend fun obtenerResumen(): ResumenResponse

    @DELETE("gastos/{id}")
    suspend fun eliminarGasto(@Path("id") id: Int)
}
