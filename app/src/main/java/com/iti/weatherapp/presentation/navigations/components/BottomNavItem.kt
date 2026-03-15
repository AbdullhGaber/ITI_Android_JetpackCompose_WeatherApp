package com.iti.weatherapp.presentation.navigations.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.iti.weatherapp.R
import com.iti.weatherapp.presentation.navigations.Alerts
import com.iti.weatherapp.presentation.navigations.Favorites
import com.iti.weatherapp.presentation.navigations.Home
import com.iti.weatherapp.presentation.navigations.Settings

data class BottomNavItem(
    val title: String,
    val route: Any,
    val icon: ImageVector
)

@Composable
fun getBottomNavItems() = listOf(
    BottomNavItem(stringResource(R.string.nav_home), Home, Icons.Default.Home),
    BottomNavItem(stringResource(R.string.nav_favorites), Favorites, Icons.Default.Favorite),
    BottomNavItem(stringResource(R.string.nav_alerts), Alerts, Icons.Default.Notifications),
    BottomNavItem(stringResource(R.string.nav_settings), Settings, Icons.Default.Settings)
)