package com.example.countries.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countries.domain.model.CountrySummary
import com.example.countries.domain.model.Region
import com.example.countries.domain.repository.CountryRepository
import com.example.countries.domain.repository.NotesRepository
import com.example.countries.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class CountryListViewModel @Inject constructor(
    private val countryRepository: CountryRepository,
    private val notesRepository: NotesRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val status = MutableStateFlow(RefreshStatus())

    private data class RefreshStatus(val isRefreshing: Boolean = false, val error: String? = null)

    private data class Inputs(
        val countries: List<CountrySummary>,
        val query: String,
        val region: Region,
        val noteCodes: Set<String>
    )

    private val inputs: Flow<Inputs> = combine(
        countryRepository.observeCountries(),
        query.debounce(300).distinctUntilChanged(),
        settingsRepository.preferences.map { it.region }.distinctUntilChanged(),
        notesRepository.observeNotes().map { notes -> notes.map { it.countryCode }.toSet() }
    ) { countries, text, region, noteCodes ->
        Inputs(countries, text.trim(), region, noteCodes)
    }

    val uiState: StateFlow<CountryListUiState> = combine(
        inputs,
        status,
        countryRepository.observeLastSync()
    ) { input, refresh, lastSync ->
        val filtered = input.countries
            .filter { input.region == Region.ALL || it.region.equals(input.region.apiName, ignoreCase = true) }
            .filter { input.query.isBlank() || it.commonName.contains(input.query, ignoreCase = true) }

        val content = when {
            filtered.isNotEmpty() -> ListContent.Success(filtered)
            refresh.isRefreshing && input.countries.isEmpty() -> ListContent.Loading
            input.countries.isEmpty() && refresh.error != null ->
                ListContent.Error("No offline data yet. ${refresh.error}")
            else -> ListContent.Empty
        }

        CountryListUiState(
            query = input.query,
            region = input.region,
            isRefreshing = refresh.isRefreshing,
            lastSync = lastSync,
            errorMessage = refresh.error,
            noteCodes = input.noteCodes,
            content = content
        )
    }
        .catch { error ->
            emit(CountryListUiState(content = ListContent.Error(error.message ?: "Unexpected error")))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CountryListUiState())

    init {
        viewModelScope.launch {
            if (!countryRepository.hasCache()) {
                refresh()
            }
        }
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onSelectRegion(region: Region) {
        viewModelScope.launch { settingsRepository.setRegion(region) }
    }

    fun onRefresh() {
        refresh()
    }

    fun onRetry() {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            status.value = RefreshStatus(isRefreshing = true, error = null)
            val result = countryRepository.refresh()
            status.value = RefreshStatus(
                isRefreshing = false,
                error = result.exceptionOrNull()?.message
            )
        }
    }
}
