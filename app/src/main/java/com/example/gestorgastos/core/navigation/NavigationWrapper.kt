package com.example.gestorgastos.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationWrapper(navGraphs: List<FeatureNavGraph>) {
    val navController = rememberNavController()
    
    // Siempre iniciamos en Login para asegurar el flujo correcto
    NavHost(
        navController = navController,
        startDestination = Login
    ) {
        navGraphs.forEach { graph ->
            graph.registerGraph(this, navController)
        }
    }
}
