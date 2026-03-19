package com.iti.weatherapp.presentation.screens.alerts

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.weatherapp.data.local.db.entities.WeatherAlert
import com.iti.weatherapp.data.local.preferences.SettingsPreferences
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.presentation.utils.alarm_notifications.WeatherAlarmScheduler
import com.iti.weatherapp.presentation.utils.oem.MiuiAutoStartStrategy
import com.iti.weatherapp.presentation.utils.oem.MiuiSpecialPermissionStrategy
import com.iti.weatherapp.presentation.utils.oem.OemPermissionManager
import com.iti.weatherapp.presentation.utils.oem.OemPermissionStrategy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val repository: Repository,
    private val alarmScheduler: WeatherAlarmScheduler,
    private val settingsPreferences: SettingsPreferences,
    private val oemPermissionManager: OemPermissionManager
) : ViewModel() {

    val alertsList: StateFlow<List<WeatherAlert>> = repository.getAllWeatherAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val hasAskedAutoStart: StateFlow<Boolean> = settingsPreferences.hasAskedAutoStartFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val hasAskedOemPermission: StateFlow<Boolean> = settingsPreferences.hasAskedOemPermissionFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    var showBottomSheet by mutableStateOf(false)
        private set

    var showDeleteDialog by mutableStateOf(false)
        private set

    var alertToDelete by mutableStateOf<WeatherAlert?>(null)
        private set

    var pendingAlertToSave by mutableStateOf<WeatherAlert?>(null)
        private set

    var activeStrategy by mutableStateOf<OemPermissionStrategy?>(null)
        private set


    fun openBottomSheet() { showBottomSheet = true }
    fun closeBottomSheet() { showBottomSheet = false }

    fun triggerDeleteDialog(alert: WeatherAlert) {
        alertToDelete = alert
        showDeleteDialog = true
    }

    fun dismissDeleteDialog() {
        showDeleteDialog = false
        alertToDelete = null
    }

    fun confirmDelete() {
        alertToDelete?.let { deleteAlert(it) }
        dismissDeleteDialog()
    }

    fun setPendingStrategy(alert: WeatherAlert?, strategy: OemPermissionStrategy?) {
        pendingAlertToSave = alert
        activeStrategy = strategy
    }

    fun clearPendingStrategy() {
        pendingAlertToSave = null
        activeStrategy = null
    }

    private fun markAutoStartAsAsked() {
        viewModelScope.launch { settingsPreferences.saveAutoStartAsked(true) }
    }

    private fun markOemPermissionAsAsked() {
        viewModelScope.launch { settingsPreferences.saveOemPermissionAsked(true) }
    }

    fun getRequiredPermissionStrategy(context: Context): OemPermissionStrategy? {
        return oemPermissionManager.getRequiredPermissionStrategy(
            context = context,
            hasAskedAutoStart = hasAskedAutoStart.value,
            hasAskedLockScreen = hasAskedOemPermission.value
        )
    }

    fun markStrategyAsAsked(strategy: OemPermissionStrategy) {
        oemPermissionManager.markAsAsked(strategy)
        when (strategy) {
            is MiuiAutoStartStrategy -> markAutoStartAsAsked()
            is MiuiSpecialPermissionStrategy -> markOemPermissionAsAsked()
        }
    }


    fun saveAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            val newlyGeneratedId = repository.insertWeatherAlert(alert)
            val savedAlertWithRealId = alert.copy(id = newlyGeneratedId.toInt())
            alarmScheduler.schedule(savedAlertWithRealId)
        }
    }

    fun deleteAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            repository.deleteWeatherAlert(alert)
            alarmScheduler.cancel(alert)
        }
    }

    fun updateAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            repository.updateWeatherAlert(alert)
            if (alert.isEnabled) {
                alarmScheduler.schedule(alert)
            } else {
                alarmScheduler.cancel(alert)
            }
        }
    }
}