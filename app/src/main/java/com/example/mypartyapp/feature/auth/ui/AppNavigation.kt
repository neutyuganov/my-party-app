    package com.example.mypartyapp.feature.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mypartyapp.feature.home.ui.MainScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = viewModel()

    val startDestination = if (viewModel.isAuthenticated) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = {
                    viewModel.clearError()
                    navController.navigate("register") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoggedIn = {
                    navController.navigate("home") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    viewModel.clearError()
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onRegistered = {
                    navController.navigate("home") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            MainScreen(
                onSignOut = {
                    viewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
