package com.example.gestorgastos.features.login.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.gestorgastos.core.navigation.FeatureNavGraph
import com.example.gestorgastos.core.navigation.Grupos
import com.example.gestorgastos.core.navigation.Login
import com.example.gestorgastos.core.navigation.Registro
import com.example.gestorgastos.features.login.di.LoginModule
import com.example.gestorgastos.features.login.presentation.screens.LoginScreen
import com.example.gestorgastos.features.login.presentation.viewmodels.LoginViewModel

class LoginNavGraph(private val loginModule: LoginModule) : FeatureNavGraph {

    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<Login> {
            val viewModel: LoginViewModel = viewModel(
                factory = loginModule.provideLoginViewModelFactory()
            )
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { navController.navigate(Grupos) },
                onGoToRegistro = { navController.navigate(Registro) }
            )
        }
    }
}
