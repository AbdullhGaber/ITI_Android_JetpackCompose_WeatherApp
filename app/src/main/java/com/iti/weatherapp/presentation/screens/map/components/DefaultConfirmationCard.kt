package com.iti.weatherapp.presentation.screens.map.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iti.weatherapp.presentation.screens.map.ConfirmationCardColors
import com.iti.weatherapp.presentation.screens.map.PickedLocation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

@Composable
internal fun DefaultConfirmationCard(
    pickedLocation: PickedLocation,
    colors: ConfirmationCardColors,
    shape: Shape,
    elevation: Dp,
    onConfirm: () -> Unit,
) {
    val labelColor = if (colors.labelColor == Color.Unspecified)
        MaterialTheme.colorScheme.primary else colors.labelColor
    val titleColor = if (colors.titleColor == Color.Unspecified)
        MaterialTheme.colorScheme.onSurface else colors.titleColor

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (colors.containerColor == Color.Unspecified)
                MaterialTheme.colorScheme.surface
            else colors.containerColor
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Selected Location",
                style = MaterialTheme.typography.labelMedium,
                color = labelColor,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = pickedLocation.cityName ?: "Custom Coordinates",
                style = MaterialTheme.typography.titleMedium,
                color = titleColor,
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                colors = if (colors.buttonContainerColor != Color.Unspecified)
                    ButtonDefaults.buttonColors(containerColor = colors.buttonContainerColor)
                else ButtonDefaults.buttonColors(),
            ) {
                Text("Confirm Location")
            }
        }
    }
}