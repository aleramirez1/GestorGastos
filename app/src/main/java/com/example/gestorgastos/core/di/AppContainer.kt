package com.example.gestorgastos.core.di

import android.content.Context
import com.example.gestorgastos.core.network.GastosApi
import com.example.gestorgastos.features.grupos.data.repositories.GruposRepositoryImpl
import com.example.gestorgastos.features.grupos.domain.repositories.GruposRepository
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import com.example.gestorgastos.features.login.data.repositories.LoginRepositoryImpl
import com.example.gestorgastos.features.login.domain.repositories.LoginRepository
import com.example.gestorgastos.features.registro.data.repositories.RegistroRepositoryImpl
import com.example.gestorgastos.features.registro.domain.repositories.RegistroRepository
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

    val loginRepository: LoginRepository by lazy {
        LoginRepositoryImpl(gastosApi, tokenManager)
    }

    val registroRepository: RegistroRepository by lazy {
        RegistroRepositoryImpl(gastosApi)
    }

    val gruposRepository: GruposRepository by lazy {
        GruposRepositoryImpl(gastosApi)
    }
}
