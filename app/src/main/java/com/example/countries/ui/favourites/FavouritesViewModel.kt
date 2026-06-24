package com.example.countries.ui.favourites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countries.domain.repository.CountryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repository: CountryRepository
) : ViewModel() {

    var uiState by mutableStateOf(FavouritesUiState())
        private set

    init {
        viewModelScope.launch {
            repository.observeFavourites().collect { favourites ->
                uiState = FavouritesUiState(isLoading = false, countries = favourites)
            }
        }
    }

    fun onRemove(code: String) {
        viewModelScope.launch {
            repository.removeFavourite(code)
        }
    }
}
