package com.example.countries.ui.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countries.domain.model.CountrySummary
import com.example.countries.domain.repository.CountryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryListViewModel @Inject constructor(
    private val repository: CountryRepository
) : ViewModel() {

    var uiState by mutableStateOf(CountryListUiState())
        private set

    private var searchJob: Job? = null

    init {
        observeFavourites()
        load(query = "")
    }

    private fun observeFavourites() {
        viewModelScope.launch {
            repository.observeFavouriteCodes().collect { codes ->
                uiState = uiState.copy(favouriteCodes = codes)
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        uiState = uiState.copy(query = newQuery)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            load(query = newQuery)
        }
    }

    fun onRetry() {
        load(query = uiState.query)
    }

    fun onRefresh() {
        viewModelScope.launch {
            uiState = uiState.copy(isRefreshing = true)
            runCatching {
                if (uiState.query.isBlank()) {
                    repository.getAllCountries(forceRefresh = true)
                } else {
                    repository.searchCountries(uiState.query.trim())
                }
            }.onSuccess { result ->
                uiState = uiState.copy(
                    isRefreshing = false,
                    content = if (result.isEmpty()) ListContent.Empty else ListContent.Success(result)
                )
            }.onFailure {
                uiState = uiState.copy(isRefreshing = false)
            }
        }
    }

    fun onToggleFavourite(country: CountrySummary) {
        viewModelScope.launch {
            if (uiState.favouriteCodes.contains(country.code)) {
                repository.removeFavourite(country.code)
            } else {
                repository.addFavourite(country)
            }
        }
    }

    private fun load(query: String) {
        viewModelScope.launch {
            uiState = uiState.copy(content = ListContent.Loading)
            runCatching {
                val trimmed = query.trim()
                if (trimmed.isBlank()) {
                    repository.getAllCountries()
                } else {
                    repository.searchCountries(trimmed)
                }
            }.onSuccess { result ->
                uiState = uiState.copy(
                    content = if (result.isEmpty()) ListContent.Empty else ListContent.Success(result)
                )
            }.onFailure { error ->
                uiState = uiState.copy(
                    content = ListContent.Error(error.toUserMessage())
                )
            }
        }
    }
}

private fun Throwable.toUserMessage(): String =
    message?.takeIf { it.isNotBlank() }
        ?.let { "Could not load countries. $it" }
        ?: "Could not load countries. Check your connection and try again."
