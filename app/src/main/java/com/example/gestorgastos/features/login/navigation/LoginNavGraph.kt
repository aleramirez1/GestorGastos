package com.example.gestorgastos.features.login.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.gestorgastos.core.navigation.FeatureNavGraph
import com.example.gestorgastos.core.navigation.Home
import com.example.gestorgastos.core.navigation.Login
import com.example.gestorgastos.core.navigation.Perfil
import com.example.gestorgastos.core.navigation.Registro
import com.example.gestorgastos.features.login.presentation.screens.LoginScreen
import com.example.gestorgastos.features.login.presentation.screens.PerfilScreen
import com.example.gestorgastos.features.login.presentation.viewmodels.LoginViewModel
import com.example.gestorgastos.features.login.presentation.viewmodels.PerfilViewModel

class LoginNavGraph : FeatureNavGraph {

    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<Login> {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { 
                    navController.navigate(Home) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                onGoToRegistro = { navController.navigate(Registro) },
                onGoToRegistroConCodigo = { codigo ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("codigo_invitacion", codigo)
                    navController.navigate(Registro)
                }
            )
        }

        navGraphBuilder.composable<Perfil> {
            val viewModel: PerfilViewModel = hiltViewModel()
            PerfilScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
