package com.example.gestorgastos.features.personas.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.gestorgastos.core.navigation.FeatureNavGraph
import com.example.gestorgastos.core.navigation.Personas
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModel
import com.example.gestorgastos.features.personas.presentation.screens.PersonasScreen

class PersonasNavGraph : FeatureNavGraph {

    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<Personas> {
            val viewModel: GruposViewModel = hiltViewModel()
            
            PersonasScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
