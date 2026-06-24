package com.example.countries.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.countries.domain.model.CountrySummary
import com.example.countries.fake.FakeListRepository
import com.example.countries.fake.FakeSettings
import com.example.countries.ui.list.CountryListScreen
import com.example.countries.ui.list.CountryListViewModel
import com.example.countries.ui.theme.CountriesExplorerTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountryListScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun summary(code: String, name: String) = CountrySummary(
        code = code,
        commonName = name,
        flagUrl = "https://flags/$code.png",
        region = "Europe",
        capital = "Capital-$code",
        population = 1_000
    )

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun errorState_retry_recoversToSuccess() {
        val repository = FakeListRepository(
            results = listOf(
                Result.failure(RuntimeException("offline")),
                Result.success(listOf(summary("FRA", "France")))
            )
        )
        val viewModel = CountryListViewModel(repository, FakeSettings())

        composeRule.setContent {
            CountriesExplorerTheme {
                CountryListScreen(
                    onCountryClick = {},
                    onFavouritesClick = {},
                    viewModel = viewModel
                )
            }
        }

        waitForText("Retry")
        composeRule.onNodeWithText("Retry").assertIsDisplayed()

        composeRule.onNodeWithText("Retry").performClick()

        waitForText("France")
        composeRule.onNodeWithText("France").assertIsDisplayed()
    }

    @Test
    fun successState_clickingCountry_passesCorrectCode() {
        val repository = FakeListRepository(
            results = listOf(
                Result.success(listOf(summary("FRA", "France"), summary("ITA", "Italy")))
            )
        )
        val viewModel = CountryListViewModel(repository, FakeSettings())
        var clickedCode: String? = null

        composeRule.setContent {
            CountriesExplorerTheme {
                CountryListScreen(
                    onCountryClick = { clickedCode = it },
                    onFavouritesClick = {},
                    viewModel = viewModel
                )
            }
        }

        waitForText("Italy")
        composeRule.onNodeWithText("Italy").performClick()

        assertEquals("ITA", clickedCode)
    }
}
