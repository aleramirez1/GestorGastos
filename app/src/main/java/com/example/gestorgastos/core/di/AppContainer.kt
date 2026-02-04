package com.example.gestorgastos.core.di

import android.content.Context
import com.example.gestorgastos.core.network.GastosApi
import com.example.gestorgastos.features.gastos.data.datasources.local.TokenManager
import com.example.gestorgastos.features.gastos.data.repositories.AuthRepositoryImpl
import com.example.gestorgastos.features.gastos.data.repositories.GastosRepositoryImpl
import com.example.gestorgastos.features.gastos.domain.repositories.AuthRepository
import com.example.gestorgastos.features.gastos.domain.repositories.GastosRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val gastosApi: GastosApi by lazy {
        retrofit.create(GastosApi::class.java)
    }

    val tokenManager: TokenManager by lazy {
        TokenManager(context)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(gastosApi, tokenManager)
    }

    val gastosRepository: GastosRepository by lazy {
        GastosRepositoryImpl(gastosApi)
    }
}
