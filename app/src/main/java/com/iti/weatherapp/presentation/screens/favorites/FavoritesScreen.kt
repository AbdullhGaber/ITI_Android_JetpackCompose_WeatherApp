package com.iti.weatherapp.presentation.screens.favorites

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.iti.weatherapp.R
import com.iti.weatherapp.data.local.db.entities.FavoriteLocation
import com.iti.weatherapp.presentation.LocalBottomPadding
import com.iti.weatherapp.presentation.components.DeleteConfirmationDialog
import com.iti.weatherapp.presentation.components.LottieIconTextView
import com.iti.weatherapp.presentation.screens.favorites.components.FavoriteItemCard

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onNavigateToMap: () -> Unit,
    onNavigateToDetails: (Double, Double, String) -> Unit
) {
    val favorites by viewModel.favoritesList.collectAsState()
    val dynamicBottomPadding = LocalBottomPadding.current

    FavoritesContent(
        favorites = favorites,
        dynamicBottomPadding = dynamicBottomPadding,
        onNavigateToMap = onNavigateToMap,
        onNavigateToDetails = onNavigateToDetails,
        onDeleteClick = { viewModel.triggerDeleteDialog(it) }
    )

    if (viewModel.showDeleteDialog) {
        DeleteConfirmationDialog(
            title = stringResource(R.string.delete_favorite_title),
            message = stringResource(R.string.delete_favorite_message),
            lottieResId = R.raw.delete_anim,
            onConfirm = { viewModel.confirmDelete() },
            onDismiss = { viewModel.dismissDeleteDialog() }
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoritesContent(
    favorites: List<FavoriteLocation>,
    dynamicBottomPadding: Dp,
    onNavigateToMap: () -> Unit,
    onNavigateToDetails: (Double, Double, String) -> Unit,
    onDeleteClick: (FavoriteLocation) -> Unit
){
    Scaffold(
        contentWindowInsets = WindowInsets(bottom = 0.dp),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToMap,
                modifier = Modifier.padding(bottom = dynamicBottomPadding),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Add Favorite")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.nav_favorites),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )

            if (favorites.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LottieIconTextView(
                        animationResId = R.raw.no_favorties,
                        message = stringResource(R.string.no_favorites),
                        modifier = Modifier.align(Center)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = dynamicBottomPadding + 16.dp)
                ) {
                    items(
                        items = favorites,
                        key = { it.id }
                    ) { location ->
                        var isVisible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { isVisible = true }
                        
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = androidx.compose.animation.scaleIn(
                                initialScale = 0.8f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ) + androidx.compose.animation.fadeIn()
                        ) {
                            FavoriteItemCard(
                                location = location,
                                onClick = { onNavigateToDetails(location.latitude, location.longitude, location.cityName) },
                                onDelete = { onDeleteClick(location) }
                            )
                        }
                    }
                }
            }
        }
    }
}