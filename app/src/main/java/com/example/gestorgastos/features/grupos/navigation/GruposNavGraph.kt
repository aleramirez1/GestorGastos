package com.example.gestorgastos.features.grupos.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.gestorgastos.core.navigation.FeatureNavGraph
import com.example.gestorgastos.core.navigation.Grupos
import com.example.gestorgastos.core.navigation.Home
import com.example.gestorgastos.core.navigation.Login
import com.example.gestorgastos.core.navigation.Perfil
import com.example.gestorgastos.core.navigation.Personas
import com.example.gestorgastos.core.navigation.Ruleta
import com.example.gestorgastos.features.grupos.presentation.screens.GruposScreen
import com.example.gestorgastos.features.grupos.presentation.screens.HomeScreen
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModel

class GruposNavGraph : FeatureNavGraph {

    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        
        navGraphBuilder.composable<Home> {
            val viewModel: GruposViewModel = hiltViewModel()
            HomeScreen(
                onNavigateToGrupos = { navController.navigate(Grupos) },
                onNavigateToCrearGrupo = { navController.navigate(Grupos) },
                onNavigateToPerfil = { navController.navigate(Perfil) },
                onLogout = {
                    navController.navigate(Login) {
                        popUpTo(Home) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }

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
                onNavigateToHome = {
                    navController.navigate(Home) {
                        popUpTo(Grupos) { inclusive = true }
                    }
                },
                onNavigateToPerfil = {
                    navController.navigate(Perfil)
                },
                onNavigateToRuleta = { participantes, grupoId, yaRecibieron ->
                    navController.navigate(Ruleta(participantes, grupoId, yaRecibieron))
                },
                onNavigateToVerGanadores = {
                    navController.navigate(Personas)
                }
            )
        }
    }
}
