package com.example.countries.ui.list

import com.example.countries.fake.FakeCountryRepository
import com.example.countries.util.MainDispatcherRule
import com.example.countries.util.summary
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

    private fun viewModel(repository: FakeCountryRepository) =
        CountryListViewModel(repository)

    @Test
    fun `initial state is Loading before work runs`() {
        val repository = FakeCountryRepository()

        val vm = viewModel(repository)

        assertTrue(vm.uiState.content is ListContent.Loading)
    }

    @Test
    fun `successful load produces Success state`() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeCountryRepository().apply {
            getAllResults = listOf(Result.success(listOf(summary("FRA"), summary("ITA"))))
        }

        val vm = viewModel(repository)
        advanceUntilIdle()

        val content = vm.uiState.content
        assertTrue(content is ListContent.Success)
        assertEquals(2, (content as ListContent.Success).countries.size)
    }

    @Test
    fun `failed load produces Error state`() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeCountryRepository().apply {
            getAllResults = listOf(Result.failure(RuntimeException("boom")))
        }

        val vm = viewModel(repository)
        advanceUntilIdle()

        assertTrue(vm.uiState.content is ListContent.Error)
    }

    @Test
    fun `empty result produces Empty not Success with empty list`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeCountryRepository().apply {
                getAllResults = listOf(Result.success(emptyList()))
            }

            val vm = viewModel(repository)
            advanceUntilIdle()

            assertTrue(vm.uiState.content is ListContent.Empty)
            assertFalse(vm.uiState.content is ListContent.Success)
        }

    @Test
    fun `retry after error initiates a new request and reaches Success`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeCountryRepository().apply {
                getAllResults = listOf(
                    Result.failure(RuntimeException("offline")),
                    Result.success(listOf(summary("DEU")))
                )
            }

            val vm = viewModel(repository)
            advanceUntilIdle()
            assertTrue(vm.uiState.content is ListContent.Error)
            val callsAfterError = repository.getAllCallCount

            vm.onRetry()
            advanceUntilIdle()

            assertTrue(repository.getAllCallCount > callsAfterError)
            assertTrue(vm.uiState.content is ListContent.Success)
        }

    @Test
    fun `debounced search runs only the latest query and drops the stale one`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeCountryRepository().apply {
                getAllResults = listOf(Result.success(listOf(summary("FRA"))))
                searchResults = mapOf("france" to listOf(summary("FRA")))
            }

            val vm = viewModel(repository)
            advanceUntilIdle()

            vm.onQueryChange("fra")
            advanceTimeBy(100)
            vm.onQueryChange("france")
            advanceUntilIdle()

            assertEquals(listOf("france"), repository.searchQueries)
        }

    @Test
    fun `toggling favourite twice for same country keeps a single entry`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeCountryRepository().apply {
                getAllResults = listOf(Result.success(listOf(summary("FRA"))))
            }

            val vm = viewModel(repository)
            advanceUntilIdle()

            vm.onToggleFavourite(summary("FRA"))
            advanceUntilIdle()
            vm.onToggleFavourite(summary("FRA"))
            advanceUntilIdle()

            assertEquals(1, repository.favouriteCount())
            assertTrue(vm.uiState.favouriteCodes.contains("FRA"))
        }

    @Test
    fun `favourite codes in state reflect repository emissions over time`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeCountryRepository().apply {
                getAllResults = listOf(Result.success(listOf(summary("FRA"), summary("ITA"))))
            }

            val vm = viewModel(repository)
            advanceUntilIdle()
            assertTrue(vm.uiState.favouriteCodes.isEmpty())

            vm.onToggleFavourite(summary("FRA"))
            advanceUntilIdle()
            assertEquals(setOf("FRA"), vm.uiState.favouriteCodes)

            vm.onToggleFavourite(summary("ITA"))
            advanceUntilIdle()
            assertEquals(setOf("FRA", "ITA"), vm.uiState.favouriteCodes)

            vm.onToggleFavourite(summary("FRA"))
            advanceUntilIdle()
            assertEquals(setOf("ITA"), vm.uiState.favouriteCodes)
        }
}
