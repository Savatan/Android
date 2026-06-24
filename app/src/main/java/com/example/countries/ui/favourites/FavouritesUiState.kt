package com.example.countries.ui.favourites

import com.example.countries.domain.model.CountrySummary

data class FavouritesUiState(
    val isLoading: Boolean = true,
    val countries: List<CountrySummary> = emptyList()
)
