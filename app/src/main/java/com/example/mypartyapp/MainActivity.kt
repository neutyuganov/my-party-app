package com.example.mypartyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.mypartyapp.feature.auth.ui.AppNavigation
import com.example.mypartyapp.feature.auth.ui.AuthViewModel
import com.example.mypartyapp.ui.theme.MyPartyAppTheme

class MainActivity : ComponentActivity() {

    // Тот же экземпляр ViewModel что и в Compose (один ViewModelStore на Activity)
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // installSplashScreen вызывается до super.onCreate — это требование API
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Держим системный splash пока Auth SDK проверяет сессию.
        // Примечание: на ПЕРВОМ запуске после установки Android может не нарисовать
        // иконку на splash (особенность платформы) — это косметика, не баг кода.
        splashScreen.setKeepOnScreenCondition { authViewModel.isCheckingSession }

        enableEdgeToEdge()
        setContent {
            MyPartyAppTheme {
                AppNavigation(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
