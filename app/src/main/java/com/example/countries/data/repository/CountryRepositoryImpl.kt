package com.example.countries.data.repository

import com.example.countries.data.local.FavouriteCountryDao
import com.example.countries.data.mapper.toCountryOrNull
import com.example.countries.data.mapper.toEntity
import com.example.countries.data.mapper.toSummary
import com.example.countries.data.mapper.toSummaryOrNull
import com.example.countries.data.remote.CountryApi
import com.example.countries.domain.model.Country
import com.example.countries.domain.model.CountrySummary
import com.example.countries.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryRepositoryImpl @Inject constructor(
    private val api: CountryApi,
    private val dao: FavouriteCountryDao
) : CountryRepository {

    private var cachedAll: List<CountrySummary>? = null

    override suspend fun getAllCountries(forceRefresh: Boolean): List<CountrySummary> {
        val cache = cachedAll
        if (!forceRefresh && cache != null) {
            return cache
        }
        val result = api.getAllCountries()
            .mapNotNull { it.toSummaryOrNull() }
            .sortedBy { it.commonName }
        cachedAll = result
        return result
    }

    override suspend fun searchCountries(query: String): List<CountrySummary> {
        return try {
            api.searchByName(query)
                .mapNotNull { it.toSummaryOrNull() }
                .sortedBy { it.commonName }
        } catch (e: HttpException) {
            if (e.code() == 404) emptyList() else throw e
        }
    }

    override suspend fun getCountry(code: String): Country {
        return api.getByCode(code).firstNotNullOfOrNull { it.toCountryOrNull() }
            ?: throw NoSuchElementException("Country not found for code $code")
    }

    override fun observeFavourites(): Flow<List<CountrySummary>> =
        dao.observeAll().map { list -> list.map { it.toSummary() } }

    override fun observeFavouriteCodes(): Flow<Set<String>> =
        dao.observeCodes().map { it.toSet() }

    override suspend fun addFavourite(country: CountrySummary) {
        dao.insert(country.toEntity())
    }

    override suspend fun removeFavourite(code: String) {
        dao.deleteByCode(code)
    }
}
