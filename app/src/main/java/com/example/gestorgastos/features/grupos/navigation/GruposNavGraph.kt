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
import com.example.gestorgastos.core.navigation.Perfil
import com.example.gestorgastos.core.navigation.ProgresoAhorro
import com.example.gestorgastos.core.navigation.Home
import com.example.gestorgastos.features.grupos.presentation.screens.GruposScreen
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModel
import com.example.gestorgastos.features.ruleta.presentation.screens.RuletaScreen
import com.example.gestorgastos.features.ruleta.presentation.screens.VerGanadoresRuletaScreen
import com.example.gestorgastos.features.login.presentation.screens.PerfilScreen
import com.example.gestorgastos.features.grupos.presentation.screens.ProgresoAhorroScreen
import com.example.gestorgastos.features.grupos.presentation.screens.HomeScreen

class GruposNavGraph : FeatureNavGraph {

    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<Home> {
            HomeScreen(
                onNavigateToGrupos = {
                    navController.navigate(Grupos)
                },
                onNavigateToCrearGrupo = {
                    // Navegamos a grupos y forzamos que se abra en modo crear
                    navController.navigate(Grupos) 
                },
                onNavigateToPerfil = {
                    navController.navigate(Perfil)
                },
                onLogout = {
                    navController.navigate(Login) {
                        popUpTo(Home) { inclusive = true }
                    }
                }
            )
        }

        navGraphBuilder.composable<Grupos> {
            val viewModel: GruposViewModel = hiltViewModel()
            GruposScreen(
                viewModel = viewModel,
                onLogout = {
                    navController.popBackStack()
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
        
        navGraphBuilder.composable<Ruleta> { backStackEntry ->
            val ruleta = backStackEntry.toRoute<Ruleta>()
            val viewModel: GruposViewModel = hiltViewModel()
            RuletaScreen(
                participantes = ruleta.participantes,
                personasQueYaRecibieron = ruleta.personasQueYaRecibieron,
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

        navGraphBuilder.composable<Perfil> {
            PerfilScreen(
                onBack = { navController.popBackStack() }
            )
        }

        navGraphBuilder.composable<ProgresoAhorro> { backStackEntry ->
            val route = backStackEntry.toRoute<ProgresoAhorro>()
            ProgresoAhorroScreen(
                grupoId = route.grupoId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
