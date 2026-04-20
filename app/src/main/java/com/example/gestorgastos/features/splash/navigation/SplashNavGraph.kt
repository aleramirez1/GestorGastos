package com.example.gestorgastos.features.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.gestorgastos.core.navigation.FeatureNavGraph
import com.example.gestorgastos.core.navigation.Grupos
import com.example.gestorgastos.core.navigation.Login
import com.example.gestorgastos.core.navigation.Splash
import com.example.gestorgastos.features.splash.presentation.screens.SplashScreen

class SplashNavGraph : FeatureNavGraph {

    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<Splash> {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo<Splash> { inclusive = true }
                    }
                },
                onNavigateToGrupos = {
                    navController.navigate(Grupos) {
                        popUpTo<Splash> { inclusive = true }
                    }
                }
            )
        }
    }
}
