package com.example.gestorgastos.core.di

import android.content.Context
import androidx.room.Room
import com.example.gestorgastos.core.database.GastosDatabase
import com.example.gestorgastos.core.database.dao.GrupoDao
import com.example.gestorgastos.core.hardware.data.AndroidActivityManager
import com.example.gestorgastos.core.hardware.data.AndroidAlertManager
import com.example.gestorgastos.core.hardware.data.AndroidFlashlightManager
import com.example.gestorgastos.core.hardware.data.AndroidRotationManager
import com.example.gestorgastos.core.hardware.domain.ActivityManager
import com.example.gestorgastos.core.hardware.domain.AlertManager
import com.example.gestorgastos.core.hardware.domain.FlashlightManager
import com.example.gestorgastos.core.hardware.domain.RotationManager
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

    val context: Context = context.applicationContext

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val gastosApi: GastosApi by lazy {
        retrofit.create(GastosApi::class.java)
    }

    private val database: GastosDatabase by lazy {
        Room.databaseBuilder(
            context,
            GastosDatabase::class.java,
            "gestor_gastos_db"
        ).build()
    }

    private val grupoDao: GrupoDao by lazy {
        database.grupoDao()
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
        GruposRepositoryImpl(gastosApi, grupoDao, tokenManager)
    }
    
    val alertManager: AlertManager by lazy {
        AndroidAlertManager(context)
    }
    
    val flashlightManager: FlashlightManager by lazy {
        AndroidFlashlightManager(context)
    }
    
    val rotationManager: RotationManager by lazy {
        AndroidRotationManager(context)
    }
    
    val activityManager: ActivityManager by lazy {
        AndroidActivityManager()
    }
}
