package com.example.countries.data.repository

import com.example.countries.data.local.RecentViewDao
import com.example.countries.data.local.RecentViewEntity
import com.example.countries.data.mapper.toSummary
import com.example.countries.domain.model.CountrySummary
import com.example.countries.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val dao: RecentViewDao
) : HistoryRepository {

    override fun observeRecent(limit: Int): Flow<List<CountrySummary>> =
        dao.observeRecentCountries(limit).map { list -> list.map { it.toSummary() } }

    override suspend fun recordView(code: String) {
        dao.upsert(RecentViewEntity(code, System.currentTimeMillis()))
    }

    override suspend fun clear() {
        dao.clear()
    }
}
