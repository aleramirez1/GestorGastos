package com.example.gestorgastos.features.ruleta.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.gestorgastos.core.navigation.FeatureNavGraph
import com.example.gestorgastos.core.navigation.Grupos
import com.example.gestorgastos.core.navigation.Ruleta
import com.example.gestorgastos.core.navigation.VerGanadoresRuleta
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModel
import com.example.gestorgastos.features.ruleta.presentation.screens.RuletaScreen
import com.example.gestorgastos.features.ruleta.presentation.screens.VerGanadoresRuletaScreen

class RuletaNavGraph : FeatureNavGraph {

    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<Ruleta> { backStackEntry ->
            val ruleta = backStackEntry.toRoute<Ruleta>()
            val gruposEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Grupos>()
            }
            val gruposViewModel: GruposViewModel = hiltViewModel(gruposEntry)
            
            RuletaScreen(
                participantes = ruleta.participantes,
                onBack = { navController.popBackStack() },
                onGanadorSeleccionado = { ganador ->
                    gruposViewModel.actualizarGanadorRuletaGrupo(ruleta.grupoId, ganador)
                    navController.popBackStack()
                }
            )
        }
        
        navGraphBuilder.composable<VerGanadoresRuleta> {
            VerGanadoresRuletaScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
