package com.example.countries.ui.list

import com.example.countries.domain.model.Region
import com.example.countries.fake.FakeCountryRepository
import com.example.countries.fake.FakeSettingsRepository
import com.example.countries.util.MainDispatcherRule
import com.example.countries.util.summary
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CountryListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModel(
        repository: FakeCountryRepository,
        settings: FakeSettingsRepository = FakeSettingsRepository()
    ) = CountryListViewModel(repository, settings)

    @Test
    fun `initial value before subscription is Loading`() {
        val repository = FakeCountryRepository()
        val vm = viewModel(repository)
        assertTrue(vm.uiState.value.content is ListContent.Loading)
    }

    @Test
    fun `successful load produces Success`() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeCountryRepository().apply {
            getAllResults = listOf(Result.success(listOf(summary("FRA"), summary("ITA"))))
        }
        val vm = viewModel(repository)
        backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()
        val content = vm.uiState.value.content
        assertTrue(content is ListContent.Success)
        assertEquals(2, (content as ListContent.Success).countries.size)
    }

    @Test
    fun `failed load produces Error`() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeCountryRepository().apply {
            getAllResults = listOf(Result.failure(RuntimeException("boom")))
        }
        val vm = viewModel(repository)
        backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()
        assertTrue(vm.uiState.value.content is ListContent.Error)
    }

    @Test
    fun `empty result produces Empty not Success`() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeCountryRepository().apply {
            getAllResults = listOf(Result.success(emptyList()))
        }
        val vm = viewModel(repository)
        backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()
        assertTrue(vm.uiState.value.content is ListContent.Empty)
        assertFalse(vm.uiState.value.content is ListContent.Success)
    }

    @Test
    fun `retry after error triggers a new request and reaches Success`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeCountryRepository().apply {
                getAllResults = listOf(
                    Result.failure(RuntimeException("offline")),
                    Result.success(listOf(summary("DEU")))
                )
            }
            val vm = viewModel(repository)
            backgroundScope.launch { vm.uiState.collect {} }
            advanceUntilIdle()
            assertTrue(vm.uiState.value.content is ListContent.Error)
            val callsAfterError = repository.getAllCallCount
            vm.onRetry()
            advanceUntilIdle()
            assertTrue(repository.getAllCallCount > callsAfterError)
            assertTrue(vm.uiState.value.content is ListContent.Success)
        }

    @Test
    fun `typing fast runs only the latest query and drops the stale one`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeCountryRepository().apply {
                getAllResults = listOf(Result.success(listOf(summary("FRA"))))
                searchResults = mapOf("france" to listOf(summary("FRA")))
            }
            val vm = viewModel(repository)
            backgroundScope.launch { vm.uiState.collect {} }
            advanceUntilIdle()
            vm.onQueryChange("fra")
            advanceTimeBy(100)
            vm.onQueryChange("france")
            advanceUntilIdle()
            assertEquals(listOf("france"), repository.searchQueries)
        }

    @Test
    fun `selecting a region filters the list reactively`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeCountryRepository().apply {
                getAllResults = listOf(
                    Result.success(
                        listOf(summary("FRA", region = "Europe"), summary("JPN", region = "Asia"))
                    )
                )
            }
            val vm = viewModel(repository)
            backgroundScope.launch { vm.uiState.collect {} }
            advanceUntilIdle()
            assertEquals(2, (vm.uiState.value.content as ListContent.Success).countries.size)
            vm.onSelectRegion(Region.ASIA)
            advanceUntilIdle()
            val visible = (vm.uiState.value.content as ListContent.Success).countries
            assertEquals(1, visible.size)
            assertEquals("JPN", visible.first().code)
        }

    @Test
    fun `only favourites toggle composes Room favourites into the list without reload`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeCountryRepository().apply {
                getAllResults = listOf(Result.success(listOf(summary("FRA"), summary("ITA"))))
            }
            val vm = viewModel(repository)
            backgroundScope.launch { vm.uiState.collect {} }
            advanceUntilIdle()
            vm.onToggleFavourite(summary("FRA"))
            advanceUntilIdle()
            val callsBeforeToggle = repository.getAllCallCount
            vm.onToggleOnlyFavourites()
            advanceUntilIdle()
            val visible = (vm.uiState.value.content as ListContent.Success).countries
            assertEquals(1, visible.size)
            assertEquals("FRA", visible.first().code)
            assertEquals(callsBeforeToggle, repository.getAllCallCount)
        }

    @Test
    fun `favourites from Room update state without manual reload`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeCountryRepository().apply {
                getAllResults = listOf(Result.success(listOf(summary("FRA"), summary("ITA"))))
            }
            val vm = viewModel(repository)
            backgroundScope.launch { vm.uiState.collect {} }
            advanceUntilIdle()
            assertTrue(vm.uiState.value.favouriteCodes.isEmpty())
            vm.onToggleFavourite(summary("FRA"))
            advanceUntilIdle()
            assertEquals(setOf("FRA"), vm.uiState.value.favouriteCodes)
            vm.onToggleFavourite(summary("ITA"))
            advanceUntilIdle()
            assertEquals(setOf("FRA", "ITA"), vm.uiState.value.favouriteCodes)
        }

    @Test
    fun `toggling favourite adds then removes it`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeCountryRepository().apply {
                getAllResults = listOf(Result.success(listOf(summary("FRA"))))
            }
            val vm = viewModel(repository)
            backgroundScope.launch { vm.uiState.collect {} }
            advanceUntilIdle()
            vm.onToggleFavourite(summary("FRA"))
            advanceUntilIdle()
            assertTrue(vm.uiState.value.favouriteCodes.contains("FRA"))
            assertEquals(1, repository.favouriteCount())
            vm.onToggleFavourite(summary("FRA"))
            advanceUntilIdle()
            assertFalse(vm.uiState.value.favouriteCodes.contains("FRA"))
            assertEquals(0, repository.favouriteCount())
        }
}
