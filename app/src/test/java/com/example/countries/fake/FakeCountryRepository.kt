package com.example.countries.fake

import com.example.countries.domain.model.Country
import com.example.countries.domain.model.CountrySummary
import com.example.countries.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeCountryRepository : CountryRepository {

    var getAllResults: List<Result<List<CountrySummary>>> = listOf(Result.success(emptyList()))
    var searchResults: Map<String, List<CountrySummary>> = emptyMap()
    var getCountryResults: List<Result<Country>> = emptyList()

    var getAllCallCount = 0
        private set
    val searchQueries = mutableListOf<String>()
    var getCountryCallCount = 0
        private set

    private var getAllIndex = 0
    private var getCountryIndex = 0

    private val favourites = MutableStateFlow<Map<String, CountrySummary>>(emptyMap())

    override suspend fun getAllCountries(forceRefresh: Boolean): List<CountrySummary> {
        getAllCallCount++
        val index = getAllIndex.coerceAtMost(getAllResults.lastIndex)
        getAllIndex++
        return getAllResults[index].getOrThrow()
    }

    override suspend fun searchCountries(query: String): List<CountrySummary> {
        searchQueries.add(query)
        return searchResults[query] ?: emptyList()
    }

    override suspend fun getCountry(code: String): Country {
        getCountryCallCount++
        val index = getCountryIndex.coerceAtMost(getCountryResults.lastIndex)
        getCountryIndex++
        return getCountryResults[index].getOrThrow()
    }

    override fun observeFavourites(): Flow<List<CountrySummary>> =
        favourites.map { it.values.toList() }

    override fun observeFavouriteCodes(): Flow<Set<String>> =
        favourites.map { it.keys }

    override suspend fun addFavourite(country: CountrySummary) {
        favourites.update { it + (country.code to country) }
    }

    override suspend fun removeFavourite(code: String) {
        favourites.update { it - code }
    }

    fun favouriteCount(): Int = favourites.value.size
}
