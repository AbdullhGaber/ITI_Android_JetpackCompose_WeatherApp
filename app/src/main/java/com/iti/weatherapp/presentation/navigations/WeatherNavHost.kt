package com.iti.weatherapp.presentation.navigations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.iti.weatherapp.R
import com.iti.weatherapp.presentation.MainViewModel
import com.iti.weatherapp.presentation.screens.alerts.AlertsScreen
import com.iti.weatherapp.presentation.screens.favorites.FavoriteDetailsScreen
import com.iti.weatherapp.presentation.screens.favorites.FavoritesScreen
import com.iti.weatherapp.presentation.screens.favorites.FavoritesViewModel
import com.iti.weatherapp.presentation.screens.home.HomeScreen
import com.iti.weatherapp.presentation.screens.map.LocationPickerScreen
import com.iti.weatherapp.presentation.screens.map.MapConfig
import com.iti.weatherapp.presentation.screens.onboarding.OnboardingScreen
import com.iti.weatherapp.presentation.screens.settings.SettingsScreen
import com.iti.weatherapp.presentation.screens.settings.SettingsViewModel
import com.iti.weatherapp.presentation.utils.LocationUtils

@Composable
fun WeatherNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val favoritesViewModel: FavoritesViewModel = hiltViewModel()
    val customLocation by settingsViewModel.customMapLocationState.collectAsState()

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

        composable<Alerts> {
            AlertsScreen()
        }

        composable<Settings> {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateToMap = {
                    navController.navigate(MapPick())
                }
            )
        }

        composable<MapPick> { backStackEntry ->
            val args = backStackEntry.toRoute<MapPick>()

            LocationPickerScreen(
                mapConfig = MapConfig(
                    initialLat = customLocation.first,
                    initialLon = customLocation.second
                ),
                markerPainter = painterResource(R.drawable.location_pin_ic),
                onLocationSelected = { pickedLocation ->
                    if (args.isForFavorites) {
                        if (pickedLocation.cityName.isNullOrBlank()) {
                            LocationUtils.getCityNameFromCoordinates(
                                context,
                                pickedLocation.lat,
                                pickedLocation.lon
                            ) { resolvedName ->
                                favoritesViewModel.addFavorite(
                                    resolvedName,
                                    pickedLocation.lat,
                                    pickedLocation.lon
                                )
                            }
                        } else {
                            favoritesViewModel.addFavorite(
                                pickedLocation.cityName,
                                pickedLocation.lat,
                                pickedLocation.lon
                            )
                        }
                    } else {
                        settingsViewModel.saveCustomMapLocation(pickedLocation.lat, pickedLocation.lon)
                    }
                    navController.navigateUp()
                },
                onDismiss = {
                    navController.navigateUp()
                }
            )
        }

        composable<FavoriteDetails> {
            val favoriteDetails = it.toRoute<FavoriteDetails>()

            val lat = favoriteDetails.lat
            val lon = favoriteDetails.lon

            FavoriteDetailsScreen(
                lat = lat,
                lon = lon,
                onBack = { navController.navigateUp() }
            )
        }

        composable<Onboarding> {
            OnboardingScreen(
                onFinish = {
                    mainViewModel.completeOnboarding()
                    navController.navigate(Home) {
                        popUpTo(Onboarding) { inclusive = true }
                    }
                }
            )
        }
    }
}