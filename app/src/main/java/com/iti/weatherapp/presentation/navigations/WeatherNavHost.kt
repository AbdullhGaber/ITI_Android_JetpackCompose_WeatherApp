package com.iti.weatherapp.presentation.navigations

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.iti.weatherapp.presentation.EmptyScreen
import com.iti.weatherapp.presentation.screens.home.HomeScreen
import com.iti.weatherapp.presentation.screens.settings.SettingsScreen

@Composable
fun WeatherNavHost(
    navController : NavHostController,
    startDestination: Any,
    modifier: Modifier
){
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Home> { HomeScreen() }
        composable<Favorites> { EmptyScreen("Favorites Screen") }
        composable<Alerts> { EmptyScreen("Alerts Screen") }
        composable<Settings> { SettingsScreen() }
    }
}