package com.iti.weatherapp.presentation.navigations.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.iti.weatherapp.presentation.navigations.Alerts
import com.iti.weatherapp.presentation.navigations.Favorites
import com.iti.weatherapp.presentation.navigations.Home
import com.iti.weatherapp.presentation.navigations.Settings

data class BottomNavItem(
    val title: String,
    val route: Any,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Home, Icons.Default.Home),
    BottomNavItem("Favorites", Favorites, Icons.Default.Favorite),
    BottomNavItem("Alerts", Alerts, Icons.Default.Notifications),
    BottomNavItem("Settings", Settings, Icons.Default.Settings)
)