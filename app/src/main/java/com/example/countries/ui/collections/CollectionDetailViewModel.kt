package com.example.countries.ui.collections

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countries.domain.model.CollectionSummary
import com.example.countries.domain.model.CountrySummary
import com.example.countries.domain.repository.CollectionsRepository
import com.example.countries.ui.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CollectionDetailUiState(
    val collection: CollectionSummary? = null,
    val countries: List<CountrySummary> = emptyList()
)

@HiltViewModel
class CollectionDetailViewModel @Inject constructor(
    private val collectionsRepository: CollectionsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val collectionId: Long = checkNotNull(savedStateHandle[Destinations.COLLECTION_ARG_ID])

    val uiState: StateFlow<CollectionDetailUiState> = combine(
        collectionsRepository.observeCollection(collectionId),
        collectionsRepository.observeCollectionCountries(collectionId)
    ) { collection, countries ->
        CollectionDetailUiState(collection = collection, countries = countries)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CollectionDetailUiState())

    fun onRemove(code: String) {
        viewModelScope.launch { collectionsRepository.removeFromCollection(collectionId, code) }
    }
}
