package com.example.mypartyapp.feature.auth.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mypartyapp.feature.home.ui.MainScreen

private sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val viewModel: AuthViewModel = viewModel()

    // Пока Auth SDK инициализируется — показываем пустой экран.
    // Он не виден: системный splash лежит поверх до isCheckingSession = false.
    if (viewModel.isCheckingSession) {
        Box(modifier = modifier.fillMaxSize())
        return
    }

    val navController = rememberNavController()

    // NavHost создаётся только после awaitInitialization — isAuthenticated уже финальный.
    // remember без ключей вычисляется один раз: именно в этот момент значение корректно.
    val startDestination = remember {
        if (viewModel.isAuthenticated) Screen.Home.route else Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = {
                    viewModel.clearError()
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onLoggedIn = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    viewModel.clearError()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onRegistered = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            MainScreen(
                onSignOut = {
                    viewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
