package com.example.gestorgastos.features.invitaciones.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.gestorgastos.core.navigation.FeatureNavGraph
import com.example.gestorgastos.core.navigation.Invitaciones
import com.example.gestorgastos.features.invitaciones.presentation.screens.InvitacionesScreen

class InvitacionesNavGraph : FeatureNavGraph {
    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<Invitaciones> { backStackEntry ->
            val invitaciones = backStackEntry.toRoute<Invitaciones>()
            InvitacionesScreen(
                telefono = invitaciones.telefono,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
