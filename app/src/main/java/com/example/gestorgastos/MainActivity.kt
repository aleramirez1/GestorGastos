package com.example.gestorgastos

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gestorgastos.core.navigation.NavigationWrapper
import com.example.gestorgastos.core.ui.theme.GestorGastosTheme
import com.example.gestorgastos.features.grupos.navigation.GruposNavGraph
import com.example.gestorgastos.features.login.navigation.LoginNavGraph
import com.example.gestorgastos.features.personas.navigation.PersonasNavGraph
import com.example.gestorgastos.features.registro.navigation.RegistroNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private var rotationEnabled = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
        val navGraphs = listOf(
            LoginNavGraph(),
            RegistroNavGraph(),
            GruposNavGraph(),
            PersonasNavGraph()
        )

        setContent {
            GestorGastosTheme {
                NavigationWrapper(navGraphs)
            }
        }
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Mantener la orientación actual si la rotación está habilitada
        if (!rotationEnabled) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
    
    companion object {
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
