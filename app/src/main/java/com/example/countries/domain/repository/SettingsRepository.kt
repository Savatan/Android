package com.example.countries.domain.repository

import com.example.countries.domain.model.Region
import com.example.countries.domain.model.ThemeMode
import com.example.countries.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val preferences: Flow<UserPreferences>

    suspend fun setThemeMode(mode: ThemeMode)

    suspend fun setRegion(region: Region)

    suspend fun setAutoRefresh(enabled: Boolean)

    suspend fun setRefreshIntervalHours(hours: Int)
}
