package com.example.mypartyapp.feature.home.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.mypartyapp.feature.events.ui.FeedScreen
import com.example.mypartyapp.feature.profile.ui.ProfileScreen

private enum class MainTab { FEED, PROFILE }

@Composable
fun MainScreen(onSignOut: () -> Unit) {
    // rememberSaveable сохраняет выбранную вкладку при повороте экрана
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.FEED) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == MainTab.FEED,
                    onClick = { selectedTab = MainTab.FEED },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Главная") }
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.PROFILE,
                    onClick = { selectedTab = MainTab.PROFILE },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Профиль") }
                )
            }
        }
    ) { paddingValues ->
        // Crossfade анимирует переход между вкладками вместо мгновенной замены
        Crossfade(targetState = selectedTab, label = "tab_transition") { tab ->
            when (tab) {
                MainTab.FEED -> FeedScreen(modifier = Modifier.padding(paddingValues))
                MainTab.PROFILE -> ProfileScreen(
                    modifier = Modifier.padding(paddingValues),
                    onSignOut = onSignOut
                )
            }
        }
    }
}
