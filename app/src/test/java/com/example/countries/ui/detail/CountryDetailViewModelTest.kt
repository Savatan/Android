package com.example.countries.ui.detail

import androidx.lifecycle.SavedStateHandle
import com.example.countries.fake.FakeCountryRepository
import com.example.countries.ui.navigation.Destinations
import com.example.countries.util.MainDispatcherRule
import com.example.countries.util.country
import com.example.countries.util.summary
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
        repository: FakeCountryRepository,
        code: String = "FRA"
    ) = CountryDetailViewModel(
        repository = repository,
        savedStateHandle = SavedStateHandle(mapOf(Destinations.DETAIL_ARG_CODE to code))
    )

    @Test
    fun `loads country for the code taken from SavedStateHandle`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeCountryRepository().apply {
                getCountryResults = listOf(Result.success(country("FRA", "France")))
            }

            val vm = viewModel(repository, code = "FRA")
            advanceUntilIdle()

            val content = vm.uiState.content
            assertTrue(content is DetailContent.Success)
            assertEquals("France", (content as DetailContent.Success).country.commonName)
        }

    @Test
    fun `load failure produces Error state`() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeCountryRepository().apply {
            getCountryResults = listOf(Result.failure(RuntimeException("offline")))
        }

        val vm = viewModel(repository)
        advanceUntilIdle()

        assertTrue(vm.uiState.content is DetailContent.Error)
    }

    @Test
    fun `retry after error initiates a new request and reaches Success`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeCountryRepository().apply {
                getCountryResults = listOf(
                    Result.failure(RuntimeException("offline")),
                    Result.success(country("FRA"))
                )
            }

            val vm = viewModel(repository)
            advanceUntilIdle()
            assertTrue(vm.uiState.content is DetailContent.Error)
            val callsAfterError = repository.getCountryCallCount

            vm.onRetry()
            advanceUntilIdle()

            assertTrue(repository.getCountryCallCount > callsAfterError)
            assertTrue(vm.uiState.content is DetailContent.Success)
        }

    @Test
    fun `toggling favourite updates favourite flag`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeCountryRepository().apply {
                getCountryResults = listOf(Result.success(country("FRA")))
            }

            val vm = viewModel(repository)
            advanceUntilIdle()
            assertFalse(vm.uiState.isFavourite)

            vm.onToggleFavourite()
            advanceUntilIdle()
            assertTrue(vm.uiState.isFavourite)

            vm.onToggleFavourite()
            advanceUntilIdle()
            assertFalse(vm.uiState.isFavourite)
        }
}
