package com.example.gestorgastos.features.grupos.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.gestorgastos.core.di.AppContainer
import com.example.gestorgastos.core.navigation.FeatureNavGraph
import com.example.gestorgastos.core.navigation.Grupos
import com.example.gestorgastos.core.navigation.Login
import com.example.gestorgastos.core.navigation.Personas
import com.example.gestorgastos.core.navigation.Ruleta
import com.example.gestorgastos.core.navigation.VerGanadoresRuleta
import com.example.gestorgastos.features.grupos.presentation.screens.GruposScreen
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModel
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModelFactory
import com.example.gestorgastos.features.ruleta.presentation.screens.RuletaScreen
import com.example.gestorgastos.features.ruleta.presentation.screens.VerGanadoresRuletaScreen

class GruposNavGraph(
    private val appContainer: AppContainer
) : FeatureNavGraph {

    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<Grupos> {
            val viewModel: GruposViewModel = viewModel(
                factory = GruposViewModelFactory(
                    appContainer.gruposRepository,
                    appContainer.tokenManager,
                    null, // CameraManager puede ser null por ahora
                    appContainer.alertManager,
                    appContainer.flashlightManager
                )
            )
            GruposScreen(
                viewModel = viewModel,
                onLogout = {
                    appContainer.tokenManager.clearAll()
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
            val viewModel: GruposViewModel = viewModel(
                factory = GruposViewModelFactory(
                    appContainer.gruposRepository,
                    appContainer.tokenManager,
                    null,
                    appContainer.alertManager,
                    appContainer.flashlightManager
                )
            )
            RuletaScreen(
                participantes = ruleta.participantes,
                onBack = { navController.popBackStack() },
                onGanadorSeleccionado = { ganador ->
                    // Actualizar el grupo con el ganador
                    viewModel.actualizarGanadorRuletaGrupo(ruleta.grupoId, ganador)
                    // Volver a la pantalla de grupos
                    navController.popBackStack()
                }
            )
        }
        
        navGraphBuilder.composable<VerGanadoresRuleta> {
            val viewModel: GruposViewModel = viewModel(
                factory = GruposViewModelFactory(
                    appContainer.gruposRepository,
                    appContainer.tokenManager,
                    null,
                    appContainer.alertManager,
                    appContainer.flashlightManager
                )
            )
            VerGanadoresRuletaScreen(
                grupos = viewModel.uiState.value.grupos,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
