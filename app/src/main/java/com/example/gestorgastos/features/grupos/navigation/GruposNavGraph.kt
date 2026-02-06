package com.example.gestorgastos.features.grupos.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.gestorgastos.core.navigation.FeatureNavGraph
import com.example.gestorgastos.core.navigation.Grupos
import com.example.gestorgastos.core.navigation.Login
import com.example.gestorgastos.features.grupos.di.GruposModule
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestorgastos.features.grupos.presentation.screens.GruposScreen

class GruposNavGraph(
    private val gruposModule: GruposModule,
    private val tokenManager: TokenManager
) : FeatureNavGraph {

    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<Grupos> {
            GruposScreen(
                factory = gruposModule.provideGruposViewModelFactory(),
                onLogout = {
                    tokenManager.clearAll()
                    navController.navigate(Login) {
                        popUpTo(Grupos) { inclusive = true }
                    }
                }
            )
        }
    }
}
