package com.example.countries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countries.domain.model.ThemeMode
import com.example.countries.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    settingsRepository: SettingsRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsRepository.preferences
        .map { it.themeMode }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.SYSTEM)
}
