package com.example.countries.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countries.domain.model.Region
import com.example.countries.domain.model.ThemeMode
import com.example.countries.domain.model.UserPreferences
import com.example.countries.domain.repository.SettingsRepository
import com.example.countries.work.CountriesWorkScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val workScheduler: CountriesWorkScheduler
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = settingsRepository.preferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserPreferences())

    fun onThemeSelected(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun onRegionSelected(region: Region) {
        viewModelScope.launch { settingsRepository.setRegion(region) }
    }

    fun onAutoRefreshChanged(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setAutoRefresh(enabled) }
    }

    fun onIntervalSelected(hours: Int) {
        viewModelScope.launch {
            settingsRepository.setRefreshIntervalHours(hours)
            workScheduler.schedulePeriodicRefresh(hours)
        }
    }
}
