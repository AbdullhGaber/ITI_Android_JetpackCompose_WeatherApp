package com.iti.weatherapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iti.weatherapp.presentation.navigations.Home
import com.iti.weatherapp.presentation.navigations.TelegramBottomNavItem
import com.iti.weatherapp.presentation.navigations.WeatherNavHost
import com.iti.weatherapp.presentation.navigations.components.bottomNavItems
import com.iti.weatherapp.presentation.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            WeatherAppTheme {
                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            windowInsets = WindowInsets(
                                bottom = 16.dp
                            ),
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .border(border = BorderStroke(width = 0.4.dp, color = Color.Black.copy(0.15f),), shape = RoundedCornerShape(24.dp)),
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            tonalElevation = 16.dp,
                        ){
                            bottomNavItems.forEach { item ->
                                val isSelected = currentDestination?.hierarchy?.any {
                                    it.hasRoute(item.route::class)
                                } == true

                                TelegramBottomNavItem(
                                    title = item.title,
                                    icon = item.icon,
                                    isSelected = isSelected,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(Home) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    WeatherNavHost(
                        navController = navController,
                        startDestination = Home,
                        modifier = Modifier.padding(
                            top = innerPadding.calculateTopPadding(),
                            start = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}