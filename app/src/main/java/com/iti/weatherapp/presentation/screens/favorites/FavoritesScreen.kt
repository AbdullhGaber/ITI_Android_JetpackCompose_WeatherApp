package com.iti.weatherapp.presentation.screens.favorites

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.iti.weatherapp.R
import com.iti.weatherapp.presentation.LocalBottomPadding
import com.iti.weatherapp.presentation.components.EmptyStateView
import com.iti.weatherapp.presentation.screens.favorites.components.FavoriteItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onNavigateToMap: () -> Unit,
    onNavigateToDetails: (Double, Double, String) -> Unit
) {
    val favorites by viewModel.favoritesList.collectAsState()
    val dynamicBottomPadding = LocalBottomPadding.current

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
                EmptyStateView(
                    animationResId = R.raw.no_favorties,
                    message = stringResource(R.string.no_favorites),
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = dynamicBottomPadding + 16.dp)
                ) {
                    items(
                        items = favorites,
                        key = { it.id }
                    ) { location ->
                        FavoriteItemCard(
                            location = location,
                            onClick = { onNavigateToDetails(location.latitude, location.longitude, location.cityName) },
                            onDelete = { viewModel.removeFavorite(location) }
                        )
                    }
                }
            }
        }
    }
}