package com.example.countries.ui.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countries.domain.model.CollectionSummary
import com.example.countries.domain.repository.CollectionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val collectionsRepository: CollectionsRepository
) : ViewModel() {

    val collections: StateFlow<List<CollectionSummary>> =
        collectionsRepository.observeCollections()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onCreate(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { collectionsRepository.createCollection(name) }
    }

    fun onDelete(id: Long) {
        viewModelScope.launch { collectionsRepository.deleteCollection(id) }
    }
}
