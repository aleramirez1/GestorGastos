package com.example.gestorgastos.features.grupos.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.gestorgastos.core.navigation.FeatureNavGraph
import com.example.gestorgastos.core.navigation.Grupos
import com.example.gestorgastos.core.navigation.Login
import com.example.gestorgastos.core.navigation.Personas
import com.example.gestorgastos.core.navigation.Ruleta
import com.example.gestorgastos.features.grupos.presentation.screens.GruposScreen
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModel

class GruposNavGraph : FeatureNavGraph {

    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<Grupos> {
            val viewModel: GruposViewModel = hiltViewModel()
            val context = androidx.compose.ui.platform.LocalContext.current
            androidx.compose.runtime.LaunchedEffect(Unit) {
                viewModel.procesarCodigoInvitacionPendiente(context)
            }
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
    }
}
