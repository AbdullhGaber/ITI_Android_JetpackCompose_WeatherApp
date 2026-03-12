package com.iti.weatherapp.presentation.navigations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.iti.weatherapp.presentation.EmptyScreen
import com.iti.weatherapp.presentation.screens.favorites.FavoriteDetailsScreen
import com.iti.weatherapp.presentation.screens.favorites.FavoritesScreen
import com.iti.weatherapp.presentation.screens.favorites.FavoritesViewModel
import com.iti.weatherapp.presentation.screens.home.HomeScreen
import com.iti.weatherapp.presentation.screens.map.MapPickScreen
import com.iti.weatherapp.presentation.screens.settings.SettingsScreen
import com.iti.weatherapp.presentation.screens.settings.SettingsViewModel
import com.iti.weatherapp.presentation.utils.LocationUtils

@Composable
fun WeatherNavHost(
    navController : NavHostController,
    startDestination: Any,
    modifier: Modifier
){
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val favoritesViewModel: FavoritesViewModel = hiltViewModel()
    val customLocation by settingsViewModel.customLocationState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Home> { HomeScreen() }
        composable<Favorites> {
            FavoritesScreen(
                viewModel = favoritesViewModel,
                onNavigateToMap = {
                    navController.navigate(MapPick(isForFavorites = true))
                },
                onNavigateToDetails = { lat, lon, name ->
                    navController.navigate(FavoriteDetails(lat, lon, name))
                }
            )
        }
        composable<Alerts> { EmptyScreen("Alerts Screen") }
        composable<Settings> {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateToMap = {
                    navController.navigate(MapPick)
                }
            )
        }

        composable<MapPick> { backStackEntry ->
            val args = backStackEntry.toRoute<MapPick>()

            MapPickScreen(
                initialLat = customLocation.first,
                initialLng = customLocation.second,
                onBack = { navController.navigateUp() },
                onPlacePicked = { lat, long ->
                    if (args.isForFavorites) {
                        LocationUtils.getCityNameFromCoordinates(context, lat, long){ cityName ->
                            favoritesViewModel.addFavorite(cityName, lat, long)
                        }
                    } else {
                        settingsViewModel.saveCustomLocation(lat, long)
                    }
                    navController.navigateUp()
                }
            )
        }

        composable<FavoriteDetails> {
            FavoriteDetailsScreen(onBack = { navController.navigateUp() })
        }
    }
}