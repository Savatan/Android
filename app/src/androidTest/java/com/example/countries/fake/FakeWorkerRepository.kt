package com.example.countries.fake

import com.example.countries.domain.model.Country
import com.example.countries.domain.model.CountrySummary
import com.example.countries.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeWorkerRepository(
    private val refreshResult: Result<Unit>
) : CountryRepository {

    var refreshCount = 0
        private set

    override fun observeCountries(): Flow<List<CountrySummary>> = flowOf(emptyList())
    override fun observeCountry(code: String): Flow<Country?> = flowOf(null)
    override fun observeLastSync(): Flow<Long?> = flowOf(null)
    override suspend fun hasCache(): Boolean = false
    override suspend fun isStale(ttlMillis: Long, now: Long): Boolean = true

    override suspend fun refresh(): Result<Unit> {
        refreshCount++
        return refreshResult
    }

    override suspend fun ensureDetail(code: String): Result<Unit> = Result.success(Unit)
}
