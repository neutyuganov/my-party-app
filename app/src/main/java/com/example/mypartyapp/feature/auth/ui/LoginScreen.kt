package com.example.mypartyapp.feature.auth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onNavigateToRegister: () -> Unit = {},
    onLoggedIn: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.isAuthenticated) {
        if (viewModel.isAuthenticated) onLoggedIn()
    }

    Column(modifier) {
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Пароль") })
        Button(onClick = { viewModel.signIn(email, password) }, enabled = !viewModel.isLoading) { Text("Войти") }
        if (viewModel.isLoading) {
            CircularProgressIndicator()
        }
        Button(onClick = onNavigateToRegister) {
            Text("Нет аккаунта? Зарегистрируйся")
        }
        viewModel.errorMessage?.let { Text(text = it) }
    }
}
