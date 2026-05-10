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
fun RegisterScreen(
    viewModel: AuthViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit = {},
    onRegistered: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel.isAuthenticated) {
        if (viewModel.isAuthenticated) onRegistered()
    }

    Column(modifier) {
        TextField(value = username, onValueChange = { username = it }, label = { Text("Имя пользователя") })
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Пароль") })
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Повтори пароль") }
        )
        Button(
            onClick = {
                when {
                    username.isBlank() -> localError = "Введи имя пользователя"
                    password != confirmPassword -> localError = "Пароли не совпадают"
                    else -> {
                        localError = null
                        viewModel.signUp(email, password, username.trim())
                    }
                }
            },
            enabled = !viewModel.isLoading
        ) {
            Text("Зарегистрироваться")
        }
        if (viewModel.isLoading) {
            CircularProgressIndicator()
        }
        Button(onClick = onNavigateToLogin) {
            Text("Уже есть аккаунт? Войти")
        }
        localError?.let { Text(text = it) }
        viewModel.errorMessage?.let { Text(text = it) }
    }
}
