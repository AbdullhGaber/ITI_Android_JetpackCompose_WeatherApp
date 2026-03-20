package com.iti.weatherapp.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.util.Consumer
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iti.weatherapp.presentation.navigations.Alerts
import com.iti.weatherapp.presentation.navigations.FavoriteDetails
import com.iti.weatherapp.presentation.navigations.Home
import com.iti.weatherapp.presentation.navigations.Onboarding
import com.iti.weatherapp.presentation.navigations.TelegramBottomNavItem
import com.iti.weatherapp.presentation.navigations.WeatherNavHost
import com.iti.weatherapp.presentation.navigations.components.getBottomNavItems
import com.iti.weatherapp.presentation.screens.alerts.AlarmRingingScreen
import com.iti.weatherapp.presentation.ui.theme.AppTheme
import com.iti.weatherapp.presentation.ui.theme.WeatherAppTheme
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_ALERT_ID
import com.iti.weatherapp.presentation.utils.Constants.NAVIGATE_TO_ALERTS
import dagger.hilt.android.AndroidEntryPoint

val LocalBottomPadding = compositionLocalOf { 0.dp }
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        var keepSplash = true
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { keepSplash }
        val isAlarmFiring = intent.getBooleanExtra("IS_ALARM_FIRING", false)
        val alertId = intent.getIntExtra(EXTRA_ALERT_ID, -1)


        if (isAlarmFiring) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setShowWhenLocked(true)
                setTurnScreenOn(true)
            } else {
                @Suppress("DEPRECATION")
                window.addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                )
            }
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }
        enableEdgeToEdge()
        setContent {
            viewModel = hiltViewModel()
            val appTheme by viewModel.appTheme.collectAsState(initial = AppTheme.SYSTEM_DEFAULT)

            if (isAlarmFiring && alertId != -1) {
                LaunchedEffect(Unit) {
                    keepSplash = false
                }
                WeatherAppTheme(appTheme = appTheme){
                    AlarmRingingScreen(
                        alertId = alertId,
                        onFinishActivity = {
                            finish()
                        }
                    )
                }

            }else{
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val startDestination by viewModel.startDestination.collectAsState()
                var shouldNavigateToAlerts by remember {
                    mutableStateOf(intent?.getBooleanExtra(NAVIGATE_TO_ALERTS, false) == true)
                }

                if (startDestination == null) {
                    return@setContent
                }

                LaunchedEffect(Unit) {
                    keepSplash = false
                }

                DisposableEffect(Unit) {
                    val listener = Consumer<Intent> { newIntent ->
                        if (newIntent.getBooleanExtra(NAVIGATE_TO_ALERTS, false)) {
                            shouldNavigateToAlerts = true
                        }
                    }
                    addOnNewIntentListener(listener)
                    onDispose { removeOnNewIntentListener(listener) }
                }

                LaunchedEffect(shouldNavigateToAlerts) {
                    if (shouldNavigateToAlerts) {
                        navController.navigate(Alerts) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        shouldNavigateToAlerts = false
                    }
                }

                WeatherAppTheme(appTheme = appTheme){
                    val isFavoriteDetails = currentDestination?.hierarchy?.any {
                        it.hasRoute(FavoriteDetails::class) || it.hasRoute(Onboarding::class) || it.hasRoute(com.iti.weatherapp.presentation.navigations.Splash::class)
                    } == true
                    Scaffold(
                        bottomBar = {
                            if(!isFavoriteDetails){
                                NavigationBar(
                                    modifier = Modifier
                                        .navigationBarsPadding()
                                        .padding(horizontal = 16.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .border(border = BorderStroke(width = 0.4.dp, color = Color.Black.copy(0.15f),), shape = RoundedCornerShape(24.dp)),
                                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                    tonalElevation = 16.dp,
                                ){
                                    getBottomNavItems().forEach { item ->
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
                        }
                    ) { innerPadding ->
                        CompositionLocalProvider(
                            LocalBottomPadding provides innerPadding.calculateBottomPadding()
                        ) {
                            WeatherNavHost(
                                modifier = Modifier.padding(
                                    top = innerPadding.calculateTopPadding(),
                                ),
                                navController = navController,
                                startDestination = startDestination!!,
                                mainViewModel = hiltViewModel()
                            )
                        }
                    }
                }
            }
        }
    }
}