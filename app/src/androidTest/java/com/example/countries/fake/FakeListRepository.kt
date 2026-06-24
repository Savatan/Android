package com.example.countries.fake

import com.example.countries.domain.model.Country
import com.example.countries.domain.model.CountrySummary
import com.example.countries.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeListRepository(
    private val results: List<Result<List<CountrySummary>>>
) : CountryRepository {

    private var index = 0
    private val favourites = MutableStateFlow<Map<String, CountrySummary>>(emptyMap())

    override suspend fun getAllCountries(forceRefresh: Boolean): List<CountrySummary> {
        val current = index.coerceAtMost(results.lastIndex)
        index++
        return results[current].getOrThrow()
    }

    override suspend fun searchCountries(query: String): List<CountrySummary> = emptyList()

    override suspend fun getCountry(code: String): Country =
        throw UnsupportedOperationException()

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
}
