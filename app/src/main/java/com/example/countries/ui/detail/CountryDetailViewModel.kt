package com.example.countries.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countries.domain.repository.CollectionsRepository
import com.example.countries.domain.repository.CountryRepository
import com.example.countries.domain.repository.HistoryRepository
import com.example.countries.domain.repository.NotesRepository
import com.example.countries.ui.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryDetailViewModel @Inject constructor(
    private val countryRepository: CountryRepository,
    private val notesRepository: NotesRepository,
    private val collectionsRepository: CollectionsRepository,
    private val historyRepository: HistoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val code: String = checkNotNull(savedStateHandle[Destinations.DETAIL_ARG_CODE])

    private val noteDraft = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CountryDetailUiState> = combine(
        countryRepository.observeCountry(code),
        notesRepository.observeNote(code),
        collectionsRepository.observeCollections(),
        collectionsRepository.observeCollectionsForCountry(code),
        noteDraft
    ) { country, savedNote, collections, memberCollections, draft ->
        CountryDetailUiState(
            isLoading = country == null,
            country = country,
            noteText = draft ?: savedNote?.text ?: "",
            hasSavedNote = savedNote != null,
            collections = collections,
            memberCollectionIds = memberCollections.map { it.id }.toSet()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CountryDetailUiState())

    init {
        viewModelScope.launch { historyRepository.recordView(code) }
        viewModelScope.launch { countryRepository.ensureDetail(code) }
    }

    fun onRetry() {
        viewModelScope.launch { countryRepository.ensureDetail(code) }
    }

    fun onNoteChange(value: String) {
        noteDraft.value = value
    }

    fun onSaveNote() {
        viewModelScope.launch {
            notesRepository.saveNote(code, uiState.value.noteText)
            noteDraft.value = null
        }
    }

    fun onDeleteNote() {
        viewModelScope.launch {
            notesRepository.deleteNote(code)
            noteDraft.value = null
        }
    }

    fun onToggleCollection(collectionId: Long) {
        viewModelScope.launch {
            if (uiState.value.memberCollectionIds.contains(collectionId)) {
                collectionsRepository.removeFromCollection(collectionId, code)
            } else {
                collectionsRepository.addToCollection(collectionId, code)
            }
        }
    }

    fun onCreateCollectionAndAdd(name: String) {
        viewModelScope.launch {
            val id = collectionsRepository.createCollection(name)
            collectionsRepository.addToCollection(id, code)
        }
    }
}
