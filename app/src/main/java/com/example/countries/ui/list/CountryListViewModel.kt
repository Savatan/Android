package com.example.countries.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countries.domain.model.CountrySummary
import com.example.countries.domain.model.Region
import com.example.countries.domain.repository.CountryRepository
import com.example.countries.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class CountryListViewModel @Inject constructor(
    private val repository: CountryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private data class FetchKey(val query: String, val region: Region)

    private sealed interface LoadResult {
        data object Loading : LoadResult
        data class Loaded(val countries: List<CountrySummary>) : LoadResult
        data class Failed(val message: String) : LoadResult
    }

    private val fetchKey: Flow<FetchKey> = combine(
        query.debounce(350).distinctUntilChanged(),
        settingsRepository.preferences.map { it.region }.distinctUntilChanged(),
        refreshTrigger.onStart { emit(Unit) }
    ) { text, region, _ -> FetchKey(text.trim(), region) }

    private val loadResult: Flow<LoadResult> = fetchKey.flatMapLatest { key ->
        flow {
            emit(LoadResult.Loading)
            val result = runCatching {
                val countries = if (key.query.isBlank()) {
                    repository.getAllCountries(forceRefresh = true)
                } else {
                    repository.searchCountries(key.query)
                }
                if (key.region == Region.ALL) {
                    countries
                } else {
                    countries.filter { it.region.equals(key.region.apiName, ignoreCase = true) }
                }
            }
            emit(
                result.fold(
                    onSuccess = { LoadResult.Loaded(it) },
                    onFailure = { LoadResult.Failed(it.message ?: "Unknown error") }
                )
            )
        }
    }

    val uiState: StateFlow<CountryListUiState> = combine(
        query,
        settingsRepository.preferences,
        repository.observeFavouriteCodes(),
        loadResult
    ) { text, prefs, favouriteCodes, result ->
        val content = when (result) {
            is LoadResult.Loading -> ListContent.Loading
            is LoadResult.Failed -> ListContent.Error("Could not load countries. ${result.message}")
            is LoadResult.Loaded -> {
                val visible = if (prefs.onlyFavourites) {
                    result.countries.filter { favouriteCodes.contains(it.code) }
                } else {
                    result.countries
                }
                if (visible.isEmpty()) ListContent.Empty else ListContent.Success(visible)
            }
        }
        CountryListUiState(
            query = text,
            region = prefs.region,
            onlyFavourites = prefs.onlyFavourites,
            favouriteCodes = favouriteCodes,
            content = content
        )
    }
        .catch { error ->
            emit(
                CountryListUiState(
                    content = ListContent.Error("Could not load countries. ${error.message ?: ""}")
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CountryListUiState()
        )

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onRefresh() {
        refreshTrigger.tryEmit(Unit)
    }

    fun onRetry() {
        refreshTrigger.tryEmit(Unit)
    }

    fun onSelectRegion(region: Region) {
        viewModelScope.launch { settingsRepository.setRegion(region) }
    }

    fun onToggleOnlyFavourites() {
        viewModelScope.launch {
            settingsRepository.setOnlyFavourites(!uiState.value.onlyFavourites)
        }
    }

    fun onToggleFavourite(country: CountrySummary) {
        viewModelScope.launch {
            if (uiState.value.favouriteCodes.contains(country.code)) {
                repository.removeFavourite(country.code)
            } else {
                repository.addFavourite(country)
            }
        }
    }
}
