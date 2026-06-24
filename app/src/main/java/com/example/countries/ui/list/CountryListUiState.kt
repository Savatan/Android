package com.example.countries.ui.list

import com.example.countries.domain.model.CountrySummary

sealed interface ListContent {
    data object Loading : ListContent
    data class Error(val message: String) : ListContent
    data object Empty : ListContent
    data class Success(val countries: List<CountrySummary>) : ListContent
}

data class CountryListUiState(
    val query: String = "",
    val isRefreshing: Boolean = false,
    val favouriteCodes: Set<String> = emptySet(),
    val content: ListContent = ListContent.Loading
)
