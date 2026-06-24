package com.example.countries.domain.repository

import com.example.countries.domain.model.Country
import com.example.countries.domain.model.CountrySummary
import kotlinx.coroutines.flow.Flow

interface CountryRepository {

    fun observeCountries(): Flow<List<CountrySummary>>

    fun observeCountry(code: String): Flow<Country?>

    fun observeLastSync(): Flow<Long?>

    suspend fun hasCache(): Boolean

    suspend fun isStale(ttlMillis: Long, now: Long): Boolean

    suspend fun refresh(): Result<Unit>

    suspend fun ensureDetail(code: String): Result<Unit>
}
