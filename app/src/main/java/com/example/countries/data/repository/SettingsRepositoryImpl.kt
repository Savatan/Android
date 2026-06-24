package com.example.countries.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.countries.domain.model.Region
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
        val REGION = stringPreferencesKey("region")
        val ONLY_FAVOURITES = booleanPreferencesKey("only_favourites")
    }

    override val preferences: Flow<UserPreferences> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { prefs ->
            val region = prefs[Keys.REGION]
                ?.let { stored -> runCatching { Region.valueOf(stored) }.getOrNull() }
                ?: Region.ALL
            UserPreferences(
                region = region,
                onlyFavourites = prefs[Keys.ONLY_FAVOURITES] ?: false
            )
        }

    override suspend fun setRegion(region: Region) {
        dataStore.edit { prefs -> prefs[Keys.REGION] = region.name }
    }

    override suspend fun setOnlyFavourites(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[Keys.ONLY_FAVOURITES] = enabled }
    }
}
