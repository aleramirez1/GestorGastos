package com.example.gestorgastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.gestorgastos.core.theme.GestorGastosTheme
import com.example.gestorgastos.features.gastos.di.GastosModule
import com.example.gestorgastos.features.gastos.presentation.screens.GastosScreen
import com.example.gestorgastos.features.gastos.presentation.screens.LoginScreen
import com.example.gestorgastos.features.gastos.presentation.screens.RegistroScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as GestorGastosApplication).appContainer
        val gastosModule = GastosModule(appContainer)

        setContent {
            GestorGastosTheme {
                var screen by remember {
                    mutableStateOf(
                        if (gastosModule.isLoggedIn()) "gastos" else "login"
                    )
                }

                when (screen) {
                    "login" -> LoginScreen(
                        factory = gastosModule.provideAuthViewModelFactory(),
                        onLoginSuccess = { screen = "gastos" },
                        onGoToRegistro = { screen = "registro" }
                    )
                    "registro" -> RegistroScreen(
                        factory = gastosModule.provideAuthViewModelFactory(),
                        onRegistroSuccess = { screen = "gastos" },
                        onGoToLogin = { screen = "login" }
                    )
                    "gastos" -> GastosScreen(
                        factory = gastosModule.provideGastosViewModelFactory()
                    )
                }
            }
        }
    }
}
