package com.example.gestorgastos

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.gestorgastos.core.navigation.NavigationWrapper
import com.example.gestorgastos.core.ui.theme.GestorGastosTheme
import com.example.gestorgastos.features.grupos.navigation.GruposNavGraph
import com.example.gestorgastos.features.login.navigation.LoginNavGraph
import com.example.gestorgastos.features.personas.navigation.PersonasNavGraph
import com.example.gestorgastos.features.registro.navigation.RegistroNavGraph
import com.example.gestorgastos.features.ruleta.navigation.RuletaNavGraph
import com.example.gestorgastos.features.splash.navigation.SplashNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private var rotationEnabled = false
    


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "Permiso de notificaciones concedido")
        } else {
            Log.d(TAG, "Permiso de notificaciones denegado")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
        askNotificationPermission()
        
        // Manejar deep link si viene de un email
        handleDeepLink(intent)
        
        val navGraphs = listOf(
            SplashNavGraph(),
            LoginNavGraph(),
            RegistroNavGraph(),
            GruposNavGraph(),
            PersonasNavGraph(),
            RuletaNavGraph(),
            com.example.gestorgastos.features.invitaciones.navigation.InvitacionesNavGraph()
        )

        setContent {
            GestorGastosTheme {
                NavigationWrapper(navGraphs)
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }
    
    private fun handleDeepLink(intent: Intent?) {
        val data = intent?.data
        if (data != null) {
            Log.d(TAG, "Deep link recibido: $data")
            
            // Extraer código de invitación del deep link
            // Formato: https://gestorgastos.app/invitacion/G123456789
            // o gestorgastos://invitacion/G123456789
            val path = data.path
            if (path?.startsWith("/invitacion/") == true) {
                val codigoInvitacion = path.removePrefix("/invitacion/")
                Log.d(TAG, "Código de invitación: $codigoInvitacion")
                
                // Guardar el código para mostrarlo en el diálogo
                getSharedPreferences("invitaciones", MODE_PRIVATE)
                    .edit()
                    .putString("codigo_pendiente", codigoInvitacion)
                    .apply()
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "Permiso de notificaciones ya concedido")
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (!rotationEnabled) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
    
    companion object {
        private const val TAG = "MainActivity"
        private var instance: MainActivity? = null
        
        fun getInstance(): MainActivity? = instance
    }
    
    override fun onResume() {
        super.onResume()
        instance = this
    }
    
    override fun onPause() {
        super.onPause()
        if (instance == this) {
            instance = null
        }
    }
    
    fun enableRotation() {
        rotationEnabled = true
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }
    
    fun disableRotation() {
        rotationEnabled = false
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
