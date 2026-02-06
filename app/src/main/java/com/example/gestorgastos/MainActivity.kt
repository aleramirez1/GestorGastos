package com.example.gestorgastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gestorgastos.core.navigation.NavigationWrapper
import com.example.gestorgastos.core.ui.theme.GestorGastosTheme
import com.example.gestorgastos.features.grupos.di.GruposModule
import com.example.gestorgastos.features.grupos.navigation.GruposNavGraph
import com.example.gestorgastos.features.login.di.LoginModule
import com.example.gestorgastos.features.login.navigation.LoginNavGraph
import com.example.gestorgastos.features.registro.di.RegistroModule
import com.example.gestorgastos.features.registro.navigation.RegistroNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as GestorGastosApplication).appContainer
        val loginModule = LoginModule(appContainer)
        val registroModule = RegistroModule(appContainer)
        val gruposModule = GruposModule(appContainer)

        val navGraphs = listOf(
            LoginNavGraph(loginModule),
            RegistroNavGraph(registroModule),
            GruposNavGraph(gruposModule, appContainer.tokenManager)
        )

        setContent {
            GestorGastosTheme {
                NavigationWrapper(navGraphs)
            }
        }
    }
}
