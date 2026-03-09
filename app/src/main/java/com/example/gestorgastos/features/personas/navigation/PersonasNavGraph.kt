package com.example.gestorgastos.features.personas.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.gestorgastos.core.di.AppContainer
import com.example.gestorgastos.core.navigation.FeatureNavGraph
import com.example.gestorgastos.core.navigation.Personas
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModel
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModelFactory
import com.example.gestorgastos.features.personas.presentation.screens.PersonasScreen

class PersonasNavGraph(
    private val appContainer: AppContainer
) : FeatureNavGraph {

    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<Personas> {
            val viewModel: GruposViewModel = viewModel(
                factory = GruposViewModelFactory(
                    appContainer.gruposRepository,
                    appContainer.tokenManager,
                    null,
                    appContainer.alertManager,
                    appContainer.flashlightManager
                )
            )
            
            PersonasScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
