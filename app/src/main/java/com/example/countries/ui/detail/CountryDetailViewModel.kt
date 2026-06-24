package com.example.countries.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countries.domain.model.Country
import com.example.countries.domain.model.CountrySummary
import com.example.countries.domain.repository.CountryRepository
import com.example.countries.ui.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryDetailViewModel @Inject constructor(
    private val repository: CountryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val code: String = checkNotNull(savedStateHandle[Destinations.DETAIL_ARG_CODE])

    var uiState by mutableStateOf(CountryDetailUiState())
        private set

    init {
        observeFavourite()
        load()
    }

    private fun observeFavourite() {
        viewModelScope.launch {
            repository.observeFavouriteCodes().collect { codes ->
                uiState = uiState.copy(isFavourite = codes.contains(code))
            }
        }
    }

    fun onRetry() = load()

    fun onToggleFavourite() {
        val current = uiState.content
        if (current !is DetailContent.Success) return
        viewModelScope.launch {
            if (uiState.isFavourite) {
                repository.removeFavourite(code)
            } else {
                repository.addFavourite(current.country.toSummary())
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            uiState = uiState.copy(content = DetailContent.Loading)
            runCatching { repository.getCountry(code) }
                .onSuccess { uiState = uiState.copy(content = DetailContent.Success(it)) }
                .onFailure {
                    uiState = uiState.copy(
                        content = DetailContent.Error(
                            it.message?.let { msg -> "Could not load country. $msg" }
                                ?: "Could not load country. Check your connection and try again."
                        )
                    )
                }
        }
    }
}

private fun Country.toSummary(): CountrySummary = CountrySummary(
    code = code,
    commonName = commonName,
    flagUrl = flagUrl,
    region = region,
    capital = capital,
    population = population
)
