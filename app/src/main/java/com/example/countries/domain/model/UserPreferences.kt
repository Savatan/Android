package com.example.countries.domain.model

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val region: Region = Region.ALL,
    val autoRefresh: Boolean = true,
    val refreshIntervalHours: Int = 6
)
