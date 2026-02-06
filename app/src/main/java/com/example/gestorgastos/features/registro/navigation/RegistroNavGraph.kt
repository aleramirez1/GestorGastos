package com.example.gestorgastos.features.registro.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.gestorgastos.core.navigation.FeatureNavGraph
import com.example.gestorgastos.core.navigation.Login
import com.example.gestorgastos.core.navigation.Registro
import com.example.gestorgastos.features.registro.di.RegistroModule
import com.example.gestorgastos.features.registro.presentation.screens.RegistroScreen
import com.example.gestorgastos.features.registro.presentation.viewmodels.RegistroViewModel

class RegistroNavGraph(private val registroModule: RegistroModule) : FeatureNavGraph {

    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<Registro> {
            val viewModel: RegistroViewModel = viewModel(
                factory = registroModule.provideRegistroViewModelFactory()
            )
            RegistroScreen(
                viewModel = viewModel,
                onRegistroSuccess = { navController.navigate(Login) },
                onGoToLogin = { navController.navigateUp() }
            )
        }
    }
}
