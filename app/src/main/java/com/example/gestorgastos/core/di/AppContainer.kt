package com.example.gestorgastos.core.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.gestorgastos.core.database.GastosDatabase
import com.example.gestorgastos.core.database.dao.GrupoDao
import com.example.gestorgastos.core.database.dao.SesionDao
import com.example.gestorgastos.core.database.dao.UsuarioDao
import com.example.gestorgastos.core.database.entities.SesionEntity
import com.example.gestorgastos.core.database.entities.UsuarioEntity
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
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppContainer(context: Context) {

    val context: Context = context.applicationContext

    companion object {
        private const val TAG = "ROOM_SQLITE"
    }

    private val gastosApi: GastosApi by lazy {
        retrofit.create(GastosApi::class.java)
    }

    private val database: GastosDatabase by lazy {
        Room.databaseBuilder(
            context,
            GastosDatabase::class.java,
            "gestor_gastos_db"
        ).fallbackToDestructiveMigration().build()
    }

    private val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api-gastos.freedynamicdns.net/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val grupoDao: GrupoDao by lazy {
        database.grupoDao()
    }
    
    val usuarioDao: UsuarioDao by lazy {
        database.usuarioDao()
    }
    
    val sesionDao: SesionDao by lazy {
        database.sesionDao()
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
    
    fun guardarUsuarioLocal(username: String, email: String, password: String, onResult: (Int) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val usuario = UsuarioEntity(
                username = username,
                email = email,
                passwordHash = password.hashCode().toString(),
                fechaRegistro = fecha
            )
            val id = usuarioDao.insertUsuario(usuario)
            Log.d(TAG, "REGISTRO - Usuario guardado en SQLite: $username con ID $id")
            onResult(id.toInt())
        }
    }
    
    fun guardarSesionLocal(usuarioId: Int, username: String, token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            sesionDao.cerrarTodasLasSesiones()
            val sesion = SesionEntity(
                usuarioId = usuarioId,
                username = username,
                token = token,
                fechaLogin = fecha,
                activa = true
            )
            sesionDao.insertSesion(sesion)
            usuarioDao.actualizarUltimoLogin(usuarioId, fecha)
            Log.d(TAG, "LOGIN - Sesión guardada en SQLite: $username")
        }
    }
    
    fun cerrarSesionLocal(usuarioId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            sesionDao.cerrarSesion(usuarioId)
            Log.d(TAG, "LOGOUT - Sesión cerrada en SQLite para usuario $usuarioId")
        }
    }
}
