package com.example.countries.ui.list

import com.example.countries.domain.model.Note
import com.example.countries.domain.model.Region
import com.example.countries.fake.FakeCountryRepository
import com.example.countries.fake.FakeNotesRepository
import com.example.countries.fake.FakeSettingsRepository
import com.example.countries.util.MainDispatcherRule
import com.example.countries.util.summary
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CountryListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModel(
        country: FakeCountryRepository,
        notes: FakeNotesRepository = FakeNotesRepository(),
        settings: FakeSettingsRepository = FakeSettingsRepository()
    ) = CountryListViewModel(country, notes, settings)

    @Test
    fun `cached countries are shown offline-first without a manual refresh`() =
        runTest(mainDispatcherRule.dispatcher) {
            val country = FakeCountryRepository(
                listOf(summary("FRA", "France"), summary("ITA", "Italy"))
            )
            val vm = viewModel(country)

            backgroundScope.launch { vm.uiState.collect {} }
            advanceUntilIdle()

            val content = vm.uiState.value.content
            assertTrue(content is ListContent.Success)
            assertEquals(2, (content as ListContent.Success).countries.size)
            assertEquals(0, country.refreshCount)
        }

    @Test
    fun `search query filters the cached list`() = runTest(mainDispatcherRule.dispatcher) {
        val country = FakeCountryRepository(
            listOf(summary("FRA", "France"), summary("ITA", "Italy"))
        )
        val vm = viewModel(country)

        backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.onQueryChange("ital")
        advanceUntilIdle()

        val visible = (vm.uiState.value.content as ListContent.Success).countries
        assertEquals(1, visible.size)
        assertEquals("Italy", visible.first().commonName)
    }

    @Test
    fun `region selection filters the cached list`() = runTest(mainDispatcherRule.dispatcher) {
        val country = FakeCountryRepository(
            listOf(
                summary("FRA", "France", region = "Europe"),
                summary("JPN", "Japan", region = "Asia")
            )
        )
        val vm = viewModel(country)

        backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.onSelectRegion(Region.ASIA)
        advanceUntilIdle()

        val visible = (vm.uiState.value.content as ListContent.Success).countries
        assertEquals(1, visible.size)
        assertEquals("JPN", visible.first().code)
    }

    @Test
    fun `notes compose into the list as note indicators`() = runTest(mainDispatcherRule.dispatcher) {
        val country = FakeCountryRepository(listOf(summary("FRA", "France")))
        val notes = FakeNotesRepository().apply { notesState.value = listOf(Note("FRA", "hi", 1L)) }
        val vm = viewModel(country, notes = notes)

        backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()

        assertTrue(vm.uiState.value.noteCodes.contains("FRA"))
    }

    @Test
    fun `no match produces Empty`() = runTest(mainDispatcherRule.dispatcher) {
        val country = FakeCountryRepository(listOf(summary("FRA", "France")))
        val vm = viewModel(country)

        backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.onQueryChange("zzzz")
        advanceUntilIdle()

        assertTrue(vm.uiState.value.content is ListContent.Empty)
    }

    @Test
    fun `empty cache triggers a refresh on init`() = runTest(mainDispatcherRule.dispatcher) {
        val country = FakeCountryRepository(emptyList()).apply { cachePresent = false }
        val vm = viewModel(country)

        backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()

        assertTrue(country.refreshCount >= 1)
    }
}
