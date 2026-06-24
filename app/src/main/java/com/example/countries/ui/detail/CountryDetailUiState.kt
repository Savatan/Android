package com.example.countries.ui.detail

import com.example.countries.domain.model.Country

sealed interface DetailContent {
    data object Loading : DetailContent
    data class Error(val message: String) : DetailContent
    data class Success(val country: Country) : DetailContent
}

data class CountryDetailUiState(
    val isFavourite: Boolean = false,
    val content: DetailContent = DetailContent.Loading
)
