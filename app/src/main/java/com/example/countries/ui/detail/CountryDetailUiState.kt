package com.example.countries.ui.detail

import com.example.countries.domain.model.CollectionSummary
import com.example.countries.domain.model.Country

data class CountryDetailUiState(
    val isLoading: Boolean = true,
    val country: Country? = null,
    val noteText: String = "",
    val hasSavedNote: Boolean = false,
    val collections: List<CollectionSummary> = emptyList(),
    val memberCollectionIds: Set<Long> = emptySet()
)
