package com.example.countries.data.repository

import com.example.countries.data.local.CountryCacheDao
import com.example.countries.data.mapper.toCountry
import com.example.countries.data.mapper.toDetailEntity
import com.example.countries.data.mapper.toSummary
import com.example.countries.data.mapper.toSummaryEntity
import com.example.countries.data.remote.CountryApi
import com.example.countries.domain.model.Country
import com.example.countries.domain.model.CountrySummary
import com.example.countries.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryRepositoryImpl @Inject constructor(
    private val api: CountryApi,
    private val dao: CountryCacheDao
) : CountryRepository {

    override fun observeCountries(): Flow<List<CountrySummary>> =
        dao.observeAll().map { list -> list.map { it.toSummary() } }

    override fun observeCountry(code: String): Flow<Country?> =
        dao.observeByCode(code).map { it?.toCountry() }

    override fun observeLastSync(): Flow<Long?> = dao.observeLastSync()

    override suspend fun hasCache(): Boolean = dao.count() > 0

    override suspend fun isStale(ttlMillis: Long, now: Long): Boolean =
        (dao.lastSync() ?: 0L) < now - ttlMillis

    override suspend fun refresh(): Result<Unit> = runCatching {
        val now = System.currentTimeMillis()
        val existing = dao.getAllOnce().associateBy { it.code }
        val entities = api.getAllCountries().mapNotNull { dto ->
            dto.toSummaryEntity(existing[dto.cca3], now)
        }
        if (entities.isNotEmpty()) {
            dao.upsertAll(entities)
        }
    }

    override suspend fun ensureDetail(code: String): Result<Unit> = runCatching {
        val now = System.currentTimeMillis()
        val existing = dao.getByCode(code)
        val dto = api.getByCode(code).firstOrNull()
            ?: throw NoSuchElementException("Country not found for code $code")
        val entity = dto.toDetailEntity(existing, now)
            ?: throw NoSuchElementException("Country not found for code $code")
        dao.upsert(entity)
    }
}
