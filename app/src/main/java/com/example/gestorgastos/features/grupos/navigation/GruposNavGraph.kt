package com.example.gestorgastos.features.grupos.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.gestorgastos.core.navigation.FeatureNavGraph
import com.example.gestorgastos.core.navigation.Grupos
import com.example.gestorgastos.core.navigation.Login
import com.example.gestorgastos.core.navigation.Personas
import com.example.gestorgastos.core.navigation.Ruleta
import com.example.gestorgastos.core.navigation.VerGanadoresRuleta
import com.example.gestorgastos.features.grupos.presentation.screens.GruposScreen
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModel
import com.example.gestorgastos.features.ruleta.presentation.screens.RuletaScreen
import com.example.gestorgastos.features.ruleta.presentation.screens.VerGanadoresRuletaScreen

class GruposNavGraph : FeatureNavGraph {

    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<Grupos> {
            val viewModel: GruposViewModel = hiltViewModel()
            GruposScreen(
                viewModel = viewModel,
                onLogout = {
                    navController.navigate(Login) {
                        popUpTo(Grupos) { inclusive = true }
                    }
                },
                onNavigateToRuleta = { participantes, grupoId ->
                    navController.navigate(Ruleta(participantes, grupoId))
                },
                onNavigateToVerGanadores = {
                    navController.navigate(Personas)
                }
            )
        }
        
        navGraphBuilder.composable<Ruleta> { backStackEntry ->
            val ruleta = backStackEntry.toRoute<Ruleta>()
            val viewModel: GruposViewModel = hiltViewModel()
            RuletaScreen(
                participantes = ruleta.participantes,
                onBack = { navController.popBackStack() },
                onGanadorSeleccionado = { ganador ->
                    viewModel.actualizarGanadorRuletaGrupo(ruleta.grupoId, ganador)
                    navController.popBackStack()
                }
            )
        }
        
        navGraphBuilder.composable<VerGanadoresRuleta> {
            val viewModel: GruposViewModel = hiltViewModel()
            VerGanadoresRuletaScreen(
                grupos = viewModel.uiState.value.grupos,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
