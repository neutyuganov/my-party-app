package com.example.mypartyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.mypartyapp.core.network.supabaseClient
import com.example.mypartyapp.feature.auth.ui.AppNavigation
import com.example.mypartyapp.ui.theme.MyPartyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        android.util.Log.d("Supabase", "URL: ${supabaseClient.supabaseUrl}")

        enableEdgeToEdge()
        setContent {
            MyPartyAppTheme {
                AppNavigation(modifier = Modifier.fillMaxSize())
            }
        }
    }
}