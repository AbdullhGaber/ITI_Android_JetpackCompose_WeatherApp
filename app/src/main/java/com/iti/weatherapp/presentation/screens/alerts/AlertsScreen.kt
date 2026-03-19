package com.iti.weatherapp.presentation.screens.alerts

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.iti.weatherapp.R
import com.iti.weatherapp.data.local.db.entities.AlertType
import com.iti.weatherapp.data.local.db.entities.WeatherAlert
import com.iti.weatherapp.presentation.LocalBottomPadding
import com.iti.weatherapp.presentation.components.DeleteConfirmationDialog
import com.iti.weatherapp.presentation.components.LottieIconTextView
import com.iti.weatherapp.presentation.screens.alerts.components.AddAlertBottomSheetContent
import com.iti.weatherapp.presentation.screens.alerts.components.AlertItemCard
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    viewModel: AlertsViewModel = hiltViewModel()
) {
    val alerts by viewModel.alertsList.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dynamicBottomPadding = LocalBottomPadding.current
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.openBottomSheet()
        } else {
            @SuppressLint("LocalContextGetResourceValueCall")
            Toast.makeText(context, context.getString(R.string.notification_permission_is_required_to_show_alerts), Toast.LENGTH_LONG).show()
        }
    }

    val overlayPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (Settings.canDrawOverlays(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.openBottomSheet()
            }
        } else {
            Toast.makeText(context, "Overlay permission is required for Alarms.", Toast.LENGTH_SHORT).show()
        }
    }

    val showOnLockScreenPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val strategy = viewModel.activeStrategy
        val alert = viewModel.pendingAlertToSave

        strategy?.let { viewModel.markStrategyAsAsked(it) }

        if (strategy?.isPermissionGranted(context) == true) {
            alert?.let {
                viewModel.saveAlert(it)
                viewModel.closeBottomSheet()
            }
        } else {
            Toast.makeText(context, "Permission is required to wake the screen for alarms.", Toast.LENGTH_LONG).show()
        }
        viewModel.clearPendingStrategy()
    }

    val autostartBackgroundPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val strategy = viewModel.activeStrategy
        val alert = viewModel.pendingAlertToSave

        strategy?.let { viewModel.markStrategyAsAsked(it) }

        if (strategy?.isPermissionGranted(context) == true) {
            val nextStrategy = viewModel.getRequiredPermissionStrategy(context)
            if (nextStrategy != null) {
                viewModel.setPendingStrategy(alert, nextStrategy)
                showOnLockScreenPermission.launch(nextStrategy.getPermissionIntent(context))
            } else {
                alert?.let {
                    viewModel.saveAlert(it)
                    viewModel.closeBottomSheet()
                }
                viewModel.clearPendingStrategy()
            }
        } else {
            Toast.makeText(context, "Permission is required to wake the screen for alarms.", Toast.LENGTH_LONG).show()
            viewModel.clearPendingStrategy()
        }
    }

    val onAddAlertClicked = {
        if (Settings.canDrawOverlays(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    viewModel.openBottomSheet()
                } else {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                viewModel.openBottomSheet()
            }
        } else {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:${context.packageName}".toUri()
            )
            overlayPermissionLauncher.launch(intent)
        }
    }

    AlertsContent(
        alerts = alerts,
        dynamicBottomPadding = dynamicBottomPadding,
        onAddAlertClicked = onAddAlertClicked,
        onDeleteAlertClicked = { viewModel.triggerDeleteDialog(it) },
        onUpdateAlert = { viewModel.updateAlert(it) }
    )

    if (viewModel.showDeleteDialog) {
        DeleteConfirmationDialog(
            title = stringResource(R.string.delete_alert_title),
            message = stringResource(R.string.delete_alert_message),
            lottieResId = R.raw.delete_anim,
            onConfirm = { viewModel.confirmDelete() },
            onDismiss = { viewModel.dismissDeleteDialog() }
        )
    }

    if (viewModel.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeBottomSheet() },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            AddAlertBottomSheetContent(
                onSave = { newAlert ->
                    if (newAlert.alertType == AlertType.ALARM) {
                        val strategy = viewModel.getRequiredPermissionStrategy(context)

                        if (strategy != null) {
                            viewModel.setPendingStrategy(newAlert, strategy)
                            autostartBackgroundPermission.launch(strategy.getPermissionIntent(context))
                            Toast.makeText(context, strategy.getToastMessage(context), Toast.LENGTH_LONG).show()
                            return@AddAlertBottomSheetContent
                        }
                    }
                    viewModel.saveAlert(newAlert)
                    viewModel.closeBottomSheet()
                },
                onCancel = { viewModel.closeBottomSheet() }
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AlertsContent(
    alerts: List<WeatherAlert>,
    dynamicBottomPadding: Dp,
    onAddAlertClicked: () -> Unit,
    onDeleteAlertClicked: (WeatherAlert) -> Unit,
    onUpdateAlert: (WeatherAlert) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(bottom = 0.dp),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAlertClicked,
                modifier = Modifier.padding(bottom = dynamicBottomPadding),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.AddAlert, contentDescription = stringResource(R.string.add_alert))
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
                text = stringResource(R.string.alerts_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )

            if (alerts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LottieIconTextView(
                        animationResId = R.raw.no_alerts,
                        message = stringResource(R.string.no_alerts),
                        modifier = Modifier.align(Center)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(alerts, key = { it.id }) { alert ->
                        AlertItemCard(
                            alert = alert,
                            onDelete = { onDeleteAlertClicked(alert) },
                            onUpdate = { onUpdateAlert(it) }
                        )
                    }
                }
            }
        }
    }
}

fun showNativeDateTimePicker(context: Context, onDateTimeSelected: (Long) -> Unit) {
    val calendar = Calendar.getInstance()

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    onDateTimeSelected(calendar.timeInMillis / 1000)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = System.currentTimeMillis() - 1000
    }.show()
}
