package com.example.countries.fake

import com.example.countries.domain.model.Region
import com.example.countries.domain.model.UserPreferences
import com.example.countries.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeSettingsRepository : SettingsRepository {

    private val state = MutableStateFlow(UserPreferences())

    override val preferences: Flow<UserPreferences> = state

    override suspend fun setRegion(region: Region) {
        state.update { it.copy(region = region) }
    }

    override suspend fun setOnlyFavourites(enabled: Boolean) {
        state.update { it.copy(onlyFavourites = enabled) }
    }
}
