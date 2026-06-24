package com.example.countries.domain.repository

import com.example.countries.domain.model.Country
import com.example.countries.domain.model.CountrySummary
import kotlinx.coroutines.flow.Flow

interface CountryRepository {

    suspend fun getAllCountries(forceRefresh: Boolean = false): List<CountrySummary>

    suspend fun searchCountries(query: String): List<CountrySummary>

    suspend fun getCountry(code: String): Country

    fun observeFavourites(): Flow<List<CountrySummary>>

    fun observeFavouriteCodes(): Flow<Set<String>>

    suspend fun addFavourite(country: CountrySummary)

    suspend fun removeFavourite(code: String)
}
