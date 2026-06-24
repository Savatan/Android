package com.example.countries.domain.model

data class UserPreferences(
    val region: Region = Region.ALL,
    val onlyFavourites: Boolean = false
)
