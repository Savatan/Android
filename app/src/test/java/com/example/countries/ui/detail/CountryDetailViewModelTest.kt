package com.example.countries.ui.detail

import androidx.lifecycle.SavedStateHandle
import com.example.countries.fake.FakeCollectionsRepository
import com.example.countries.fake.FakeCountryRepository
import com.example.countries.fake.FakeHistoryRepository
import com.example.countries.fake.FakeNotesRepository
import com.example.countries.ui.navigation.Destinations
import com.example.countries.util.MainDispatcherRule
import com.example.countries.util.country
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CountryDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModel(
        country: FakeCountryRepository,
        notes: FakeNotesRepository = FakeNotesRepository(),
        collections: FakeCollectionsRepository = FakeCollectionsRepository(),
        history: FakeHistoryRepository = FakeHistoryRepository()
    ): CountryDetailViewModel {
        val handle = SavedStateHandle(mapOf(Destinations.DETAIL_ARG_CODE to "FRA"))
        return CountryDetailViewModel(country, notes, collections, history, handle)
    }

    @Test
    fun `country from cache is exposed in state`() = runTest(mainDispatcherRule.dispatcher) {
        val country = FakeCountryRepository().apply { countryState.value = country("FRA", "France") }
        val vm = viewModel(country)

        backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
        assertEquals("France", vm.uiState.value.country?.commonName)
    }

    @Test
    fun `opening detail records a view in history`() = runTest(mainDispatcherRule.dispatcher) {
        val country = FakeCountryRepository().apply { countryState.value = country("FRA") }
        val history = FakeHistoryRepository()
        val vm = viewModel(country, history = history)

        backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()

        assertTrue(history.recordCount >= 1)
        assertEquals("FRA", history.lastRecorded)
    }

    @Test
    fun `saving a note is reflected in state`() = runTest(mainDispatcherRule.dispatcher) {
        val country = FakeCountryRepository().apply { countryState.value = country("FRA") }
        val vm = viewModel(country)

        backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.onNoteChange("Beautiful")
        vm.onSaveNote()
        advanceUntilIdle()

        assertTrue(vm.uiState.value.hasSavedNote)
        assertEquals("Beautiful", vm.uiState.value.noteText)
    }
}
