package com.example.countries.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.countries.domain.model.Region
import com.example.countries.domain.model.ThemeMode
import com.example.countries.domain.model.UserPreferences
import com.example.countries.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object Keys {
        val THEME = stringPreferencesKey("theme_mode")
        val REGION = stringPreferencesKey("region")
        val AUTO_REFRESH = booleanPreferencesKey("auto_refresh")
        val INTERVAL = intPreferencesKey("refresh_interval_hours")
    }

    override val preferences: Flow<UserPreferences> = dataStore.data
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map { prefs ->
            UserPreferences(
                themeMode = prefs[Keys.THEME]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                    ?: ThemeMode.SYSTEM,
                region = prefs[Keys.REGION]?.let { runCatching { Region.valueOf(it) }.getOrNull() }
                    ?: Region.ALL,
                autoRefresh = prefs[Keys.AUTO_REFRESH] ?: true,
                refreshIntervalHours = prefs[Keys.INTERVAL] ?: 6
            )
        }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[Keys.THEME] = mode.name }
    }

    override suspend fun setRegion(region: Region) {
        dataStore.edit { it[Keys.REGION] = region.name }
    }

    override suspend fun setAutoRefresh(enabled: Boolean) {
        dataStore.edit { it[Keys.AUTO_REFRESH] = enabled }
    }

    override suspend fun setRefreshIntervalHours(hours: Int) {
        dataStore.edit { it[Keys.INTERVAL] = hours }
    }
}
