package com.example.countries.domain.repository

import com.example.countries.domain.model.Region
import com.example.countries.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val preferences: Flow<UserPreferences>

    suspend fun setRegion(region: Region)

    suspend fun setOnlyFavourites(enabled: Boolean)
}
